package in2000.team42.data.frost

import android.util.Log
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.auth.*
import io.ktor.client.plugins.auth.providers.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.decodeURLPart
import io.ktor.serialization.kotlinx.json.*
import in2000.team42.data.frost.model.FrostData
import in2000.team42.data.frost.model.FrostErrorResponse
import in2000.team42.data.frost.model.FrostResponse // Import FrostResponse
import in2000.team42.data.frost.model.FrostResult
import io.ktor.client.statement.HttpResponse
import io.ktor.http.isSuccess
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

class FrostDatasource() {
    private val TAG = "FrostDatasource" // LogCat tag for denne klassen
        private val CLIENTID = "5fa50311-61ee-4aa0-8f29-2262c21212e5"

    private val baseUrl = "https://frost.met.no"
    private val client = HttpClient(CIO) {
        install(Auth) {
            basic {
                credentials { BasicAuthCredentials(username = CLIENTID, password = "") }
            }
        }
    }

    suspend fun getNearestStation(latitude: Double, longitude: Double): List<String>? = withContext(Dispatchers.IO) {
        val url = "$baseUrl/sources/v0.jsonld"
        Log.d(TAG, "Searching for nearest station at coordinates: ($latitude, $longitude)")
        try {
            val response: String = client.get(url) {
                parameter("geometry", "nearest(POINT($longitude $latitude))")
                parameter("nearestmaxcount", 5) // Velger de nærmeste antall stasjonene i forhold til long og lat input
            }.body()

            Log.d(TAG, response.decodeURLPart())

            val json = Json { ignoreUnknownKeys = true }
            val data = json.decodeFromString<SourceResponse>(response)
            val stationIds = data.data.map { it.id }
            Log.i(TAG, "Found nearest station IDs: $stationIds")

            stationIds
        } catch (e: Exception) {
            Log.e(TAG, "Error finding station: ${e.message}", e)
            null
        }
    }

    /**
     * Henter info om hva som blir målt på de forskjellige stasjonene, dvs hva som kan
     * kalles på i getWeatherData
     *
     * @param stationIds Tar stationene fra getNearestStation
     * @param elements Tar gitte elementer som skal sjekkes for om stasjonene kan måle etter
     *                  kan manipulere elements i getWeatherData
     * @param refrenceTime Sjekker for de siste 24 timene
     *
     * */

    suspend fun getAvailableTimeSeries(
        stationIds: List<String>,
        elements: List<String>,
        referenceTime: String
    ): Map<String, List<String>> = withContext(Dispatchers.IO) {
        val url = "$baseUrl/observations/availableTimeSeries/v0.jsonld"
        Log.d(TAG, "Fetching available time series for stations $stationIds at time $referenceTime")
        try {

            // TODO: Noe er galt med URL kallet, kan været at URL-en må konverteres til et annet format?
            val response: HttpResponse = client.get(url) {
                parameter("sources", stationIds.joinToString(","))
                parameter("elements", elements.joinToString(","))
                parameter("referencetime", referenceTime)
            }
            if (response.status.isSuccess()) {
                val body: String = response.body()
                Log.d(TAG, body.decodeURLPart())
                val json = Json { ignoreUnknownKeys = true }
                val data = json.decodeFromString<AvailableTimeSeriesResponse>(body)
                val stationElements = data.data.groupBy({ it.sourceId }, { it.elementId })
                Log.i(TAG, "Available time series: $stationElements")
                stationElements
            } else {
                val errorBody = response.body<FrostErrorResponse>()
                Log.e(TAG, "Error fetching available time series: ${errorBody.error.reason}")
                emptyMap()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching available time series: ${e.message}", e)
            emptyMap()
        }
    }

    /**
     * Henter daglig temperatur, skydekke og regn for de siste 24 timene når funksjonen blir kalt
     * hvis det har blitt gjort målinger for hver time. Kan hende en stasjon ikke måler hver time
     * eller ikke har utstyret for å målet en type data. Altså kan det hende du ikke får noe værdata
     * på noen adresser.
     *
     * @param latitude Latitude
     * @param longitude Longitude
     *
     * @return Klasse med temperatur, skydekke og snø (eller mengden snø ekvivalent med vann på bakken)
     */
    suspend fun getWeatherData(
        stationIds: List<String>,
        referenceTime: String
    ): FrostResult = withContext(Dispatchers.IO) {
        val elements = listOf(
            "mean(air_temperature P1D)",
            "mean(surface_snow_coverage P1D)",
            "mean(cloud_area_fraction P1D)"
        )

        val availability = getAvailableTimeSeries(stationIds, elements, referenceTime)
        if (availability.isEmpty()) {
            Log.w(TAG, "No available time series found")
            return@withContext FrostResult.Failure("No available time series found")
        }

        val responses = mutableListOf<FrostResponse>()
        for (element in elements) {
            val supportingStations = availability.filter { it.value.contains(element) }.keys
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
                    } else {
                        val errorBody = response.body<FrostErrorResponse>()
                        Log.e(TAG, "Error fetching $element: ${errorBody.error.reason}")
                        return@withContext FrostResult.Failure(errorBody.error.reason) // Return error message
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Error fetching $element: ${e.message}", e)
                    return@withContext FrostResult.Failure(e.message ?: "Unknown error fetching $element")
                }
            } else {
                Log.w(TAG, "No stations support $element")
            }
        }

        val weatherData = aggregateWeatherData(responses)
        Log.i(TAG, "Aggregated ${weatherData.size} weather data points")
        FrostResult.Success(weatherData)
    }

    private fun aggregateWeatherData(responses: List<FrostResponse>): List<FrostData> {
        val dataByTime = mutableMapOf<String, FrostData.Builder>()

        for (response in responses) {
            response.data.forEach { observation ->
                val refTime = observation.referenceTime
                val builder = dataByTime.getOrPut(refTime) {
                    FrostData.Builder().setReferenceTime(refTime)
                }
                observation.observations.forEach { obs ->
                    val value = obs.value?.toDouble() // Convert Float? to Double?
                    when (obs.elementId) {
                        "mean(air_temperature P1D)" -> value?.let { builder.setTemperature(it) }
                        "mean(surface_snow_coverage P1D)" -> value?.let { builder.setPrecipitation(it) }
                        "mean(cloud_area_fraction P1D)" -> value?.let { builder.setCloudAreaFraction(it) }
                    }
                }
                if (builder.stationId == null) {
                    builder.setStationId(observation.sourceId.split(":")[0])
                }
            }
        }

        return dataByTime.values.map { it.build() }
    }

    @Serializable
    private data class SourceResponse(val data: List<Source>)
    @Serializable
    private data class Source(val id: String)

    @Serializable
    private data class AvailableTimeSeriesResponse(val data: List<TimeSeriesEntry>)
    @Serializable
    private data class TimeSeriesEntry(val sourceId: String, val elementId: String)
}