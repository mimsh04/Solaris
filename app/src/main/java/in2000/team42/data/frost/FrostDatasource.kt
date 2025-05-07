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

class FrostDatasource {
    private val TAG = "FrostDatasource" // LogCat tag for denne klassen
    private val CLIENTID = "5fa50311-61ee-4aa0-8f29-2262c21212e5"

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
     * Henter nærmeste stasjoner basert på koordinater som input. Funksjonen vil finne de nærmeste stasjonene som har målinger for elements
     * og finner de 3 nærmeste stasjonene for hvert elements. Dvs at funksjonen gjor 3 API kall
     * */

    suspend fun getNearestStation(latitude: Double, longitude: Double, referenceTime: String): Map<String, List<String>>? = withContext(Dispatchers.IO) {
        val url = "$baseUrl/sources/v0.jsonld"
        Log.d(TAG, "Searching for nearest stations at coordinates: ($latitude, $longitude)")

        /*val elements = listOf(
            temp,
            snow,
            cloudAreaFraction
        )*/

        val stationMap = mutableMapOf<String, MutableList<String>>()

        for (element in elements) {
            try {
                val response: String = client.get(url) {
                    parameter("geometry", "nearest(POINT($longitude $latitude))")
                    parameter("validtime", referenceTime)
                    parameter("nearestmaxcount", 2) // Henter de x antall naermeste stasjonene if (element == cloudAreaFraction) 1 else 3
                    parameter("elements", element)
                }.body()

                val json = Json { ignoreUnknownKeys = true }
                val data = json.decodeFromString<SourceResponse>(response)
                val stationIds = data.data.map { it.id }
                stationMap[element] = stationIds.toMutableList()
                Log.i(TAG, "Found nearest station IDs for $element: $stationIds")
            } catch (e: Exception) {
                Log.e(TAG, "Error finding stations for $element: ${e.message}", e)
                // Funksjonen vil fortsette med andre elementer hvis et element skulle ha feilet.
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
     * Henter månedlig temperatur, snø og årlig skydekke for hele 2024 når funksjonen blir kalt.
     *
     * Det kan hende noen stasjoner ikke har de nødvendige målingene og du vil få en error i logcat.
     * Skulle dette skje, gå til frost.met.no og finn API refrence. Der kan du sette inn parametere så vil API-et
     * gi en tilbakemelding på hva som gikk galt.
     *
     * Kan hende en stasjon ikke måler hver time eller ikke har utstyret for å målet en type data.
     * Altså kan det hende du ikke får noe værdata på noen adresser.
     *
     * @param stationMap Nærmeste stasjoner som har målinger på elementene eks på mapping: best_estimate_mean(air_temperature P1M) : SN18700, SN18701, SN18702
     * @param referenceTime Hvilken tid du skal ha data fra. Skrive i format AA-MM-DD/AA-MM-DD, Der start er på venstre siden av /-tegnet
     *
     * @return Flere klasser med temperatur, skydekke og snø dekke)
     */
    suspend fun getWeatherData(
        stationMap: Map<String, List<String>>,
        referenceTime: String
    ): FrostResult = withContext(Dispatchers.IO) {
        val responses = mutableListOf<FrostResponse>()

        for (element in elements) {
            val supportingStations = stationMap[element]?.filter { it.isNotEmpty() } ?: continue
            if (supportingStations.isNotEmpty()) {
                try {
                    val response: HttpResponse = client.get("$baseUrl/observations/v0.jsonld") {
                        parameter("sources", supportingStations.joinToString(","))
                        parameter("elements", element)
                        parameter("referencetime", referenceTime)
                    }
                    if (response.status.isSuccess()) {
                        val body = response.body<FrostResponse>()
                        responses.add(body)
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
                    } else {
                        val errorBody = response.body<FrostErrorResponse>()
                        Log.e(TAG, "Error fetching $element: ${errorBody.error.reason}")
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Error fetching $element: ${e.message}", e)
                }
            } else {
                Log.w(TAG, "No stations support $element")
            }
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
        // Samler alle reference times fra alle responsene
        val allRefTimes = responses.flatMap { response ->
            response.data.map { it.referenceTime }
        }.distinct().sorted()

        val dataByTime = mutableMapOf<String, FrostData>()

        // Initialiserer data med reference times
        allRefTimes.forEach { refTime ->
            dataByTime[refTime] = FrostData(
                stationId = null,
                referenceTime = refTime,
                temperature = null,
                snow = null,
                cloudAreaFraction = null
            )
        }

        // Fyller inn data fra responsene
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