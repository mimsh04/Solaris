package in2000.team42.data.frost

import android.util.Log
import in2000.team42.data.frost.model.FrostData
import in2000.team42.data.frost.model.FrostErrorResponse
import in2000.team42.data.frost.model.FrostResponse
import in2000.team42.data.frost.model.FrostResult
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BasicAuthCredentials
import io.ktor.client.plugins.auth.providers.basic
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.isSuccess
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll

class FrostDatasource {
    private val TAG = "FrostDatasource" // LogCat tag for denne klassen
    private val CLIENTID = "0791feb2-20aa-4805-9e4d-22765c3a9ff6"

    private val temp = "best_estimate_mean(air_temperature P1M)" // Opplevde problemer med å velge noe annet enn P1M
    private val snow = "mean(snow_coverage_type P1M)"
    private val cloudAreaFraction = "mean(cloud_area_fraction P1M)"
    private val elements = listOf(
        temp,
        snow,
        cloudAreaFraction
    )

    private val baseUrl = "https://frost.met.no"
    private val client = HttpClient(CIO) {
        install(Auth) {
            basic {
                credentials { BasicAuthCredentials(username = CLIENTID, password = "") }
            }
        }
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true
                coerceInputValues = true
            }, contentType = ContentType.Application.Json)
        }
    }

    /**
     * Retrieves the nearest stations based on input coordinates. The function will find the nearest stations that have measurements for elements
     * and find the 3 closest stations for each element. This means the function makes 3 API calls.
     */
    suspend fun getNearestStation(latitude: Double, longitude: Double, referenceTime: String): Map<String, List<String>>? = withContext(Dispatchers.IO) {
        val url = "$baseUrl/sources/v0.jsonld"
        Log.d(TAG, "Searching for nearest stations at coordinates: ($latitude, $longitude)")

        val stationMap = mutableMapOf<String, MutableList<String>>()

        val deferredResults = elements.map { element ->
            async {
                try {
                    val response: String = client.get(url) {
                        parameter("geometry", "nearest(POINT($longitude $latitude))")
                        parameter("validtime", referenceTime)
                        parameter("nearestmaxcount", 2)
                        parameter("elements", element)
                    }.body()

                    val json = Json { ignoreUnknownKeys = true }
                    val data = json.decodeFromString<SourceResponse>(response)
                    val stationIds = data.data.map { it.id }
                    Log.i(TAG, "Found nearest station IDs for $element: $stationIds")
                    element to stationIds
                } catch (e: Exception) {
                    Log.e(TAG, "Error finding stations for $element: ${e.message}", e)
                    element to emptyList()
                }
            }
        }

        // Wait for all requests
        deferredResults.awaitAll().forEach { (element, stationIds) ->
            if (stationIds.isNotEmpty()) {
                stationMap[element] = stationIds.toMutableList()
            }
        }

        if (stationMap.isEmpty()) {
            Log.e(TAG, "No stations found for any elements")
            null
        } else {
            stationMap
        }
    }

    /**
     * Retrieves monthly temperature, snow, and annual cloud cover for the entire year 2024 when the function is called.
     *
     * Some stations may not have the necessary measurements, and you may see an error in Logcat.
     * If this happens, visit frost.met.no and find the API reference. There, you can input parameters, and the API
     * will provide feedback on what went wrong.
     *
     * It may be that a station does not measure every hour or lacks the equipment to measure a specific type of data.
     * This means you may not get weather data for some locations.
     *
     * @param stationMap Nearest stations that have measurements for the elements, e.g., mapping: best_estimate_mean(air_temperature P1M) : SN18700, SN18701, SN18702
     * @param referenceTime The time period for which you want data. Written in the format YYYY-MM-DD/YYYY-MM-DD, where the start is on the left side of the slash
     *
     * @return Multiple classes with temperature, cloud cover, and snow cover
     */
    suspend fun getWeatherData(
        stationMap: Map<String, List<String>>,
        referenceTime: String
    ): FrostResult = withContext(Dispatchers.IO) {
        val responses = mutableListOf<FrostResponse>()

        val deferredResults = elements.map { element ->
            async {
                val supportingStations = stationMap[element]?.filter { it.isNotEmpty() } ?: return@async null
                if (supportingStations.isEmpty()) {
                    Log.w(TAG, "No stations support $element")
                    return@async null
                }

                try {
                    val response: HttpResponse = client.get("$baseUrl/observations/v0.jsonld") {
                        parameter("sources", supportingStations.joinToString(","))
                        parameter("elements", element)
                        parameter("referencetime", referenceTime)
                    }

                    if (response.status.isSuccess()) {
                        val body = response.body<FrostResponse>()
                        Log.d(TAG, "Fetched data for $element from stations $supportingStations")
                        Log.d(TAG, "Raw FrostResponse for $element:")
                        body.data.forEachIndexed { index, observation ->
                            Log.d(TAG, "  Observation [$index]:")
                            Log.d(TAG, "    Station ID: ${observation.sourceId}")
                            Log.d(TAG, "    Reference Time: ${observation.referenceTime}")
                            observation.observations.forEach { obs ->
                                Log.d(TAG, "    Element: ${obs.elementId}, Value: ${obs.value}")
                            }
                        }
                        return@async body
                    } else {
                        val errorBody = response.body<FrostErrorResponse>()
                        Log.e(TAG, "Error fetching $element: ${errorBody.error.reason}")
                        return@async null
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Error fetching $element: ${e.message}", e)
                    return@async null
                }
            }
        }

        // Wait for all requests to complete
        deferredResults.awaitAll().filterNotNull().forEach { response ->
            responses.add(response)
        }

        if (responses.isEmpty()) {
            Log.w(TAG, "No weather data retrieved")
            return@withContext FrostResult.Failure("No weather data available")
        }

        val weatherData = processWeatherData(responses)
        Log.i(TAG, "Processed ${weatherData.size} weather data points")
        FrostResult.Success(weatherData)
    }

    private fun processWeatherData(responses: List<FrostResponse>): List<FrostData> {
        // Collects all reference times from all responses
        val allRefTimes = responses.flatMap { response ->
            response.data.map { it.referenceTime }
        }.distinct().sorted()

        val dataByTime = mutableMapOf<String, FrostData>()

        // Initializes data with reference times
        allRefTimes.forEach { refTime ->
            dataByTime[refTime] = FrostData(
                stationId = null,
                referenceTime = refTime,
                temperature = null,
                snow = null,
                cloudAreaFraction = null
            )
        }

        // Fills in data from the responses
        for (response in responses) {
            response.data.forEach { observation ->
                val refTime = observation.referenceTime
                val currentData = dataByTime[refTime]!!
                observation.observations.forEach { obs ->
                    val value = obs.value?.toDouble()
                    when (obs.elementId) {
                        temp -> value?.let {
                            dataByTime[refTime] = currentData.copy(
                                stationId = observation.sourceId.split(":")[0],
                                temperature = it
                            )
                        }
                        snow -> value?.let {
                            dataByTime[refTime] = currentData.copy(
                                stationId = observation.sourceId.split(":")[0],
                                snow = it
                            )
                        }
                        cloudAreaFraction -> value?.let {
                            dataByTime[refTime] = currentData.copy(
                                stationId = observation.sourceId.split(":")[0],
                                cloudAreaFraction = it * 12.5
                            )
                        }
                    }
                }
            }
        }

        return dataByTime.values.toList()
    }

    @Serializable
    private data class SourceResponse(val data: List<Source>)
    @Serializable
    private data class Source(val id: String)
}