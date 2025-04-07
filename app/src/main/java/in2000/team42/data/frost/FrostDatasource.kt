package in2000.team42.data.frost

import android.util.Log
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.auth.*
import io.ktor.client.plugins.auth.providers.*
import io.ktor.client.request.*
import in2000.team42.data.frost.model.FrostData
import io.ktor.http.decodeURLPart
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

    suspend fun getNearestStation(latitude: Double, longitude: Double): String? = withContext(Dispatchers.IO) {
        val url = "$baseUrl/sources/v0.jsonld"
        Log.d(TAG, "Searching for nearest station at coordinates: ($latitude, $longitude)")
        try {
            val response: String = client.get(url) {
                parameter("geometry", "nearest(POINT($longitude $latitude))")
                parameter("nearestmaxcount", 5)
            }.body()

            val json = Json { ignoreUnknownKeys = true }
            val data = json.decodeFromString<SourceResponse>(response)
            val stationId = data.data.firstOrNull()?.id
            Log.i(TAG, "Found nearest station ID: $stationId")
            stationId
        } catch (e: Exception) {
            Log.e(TAG, "Error finding station: ${e.message}", e)
            null
        }
    }

    /**
     * Henter daglig temperatur, skydekke og snø for de siste 24 timene når funksjonen blir kalt
     * hvis det har blitt gjort målinger for hver time. Kan hende en stasjon ikke måler hver time
     * eller ikke har utstyret for å målet en type data. Altså kan det hende du ikke får noe værdata
     * på noen adresser.
     *
     * @param latitude Latitude
     * @param longitude Longitude
     *
     * @return Klasse med temperatur, skydekke og snø (eller mengden snø ekvivalent med vann på bakken)
     */
    suspend fun getWeatherData(stationId: String, referenceTime: String): List<FrostData> = withContext(Dispatchers.IO) {
        val referenceTimeTest = "2024-01-01/2024-12-31" // Test for å hente gjennomsnittlig data for alle månedene i 2024
        val url = "$baseUrl/observations/v0.jsonld"
        Log.d(TAG, "Fetching weather data for station $stationId at time $referenceTimeTest")
        try {
            val response: String = client.get(url) {
                parameter("sources", "SN18700"/*stationId*/)
                parameter("referencetime", "2024-01-01/2024-02-01")
                parameter("elements", "best_estimate_mean(air_temperature P1Y),mean(snow_coverage_type P1Y),mean(cloud_area_fraction P1Y)")
                parameter("timeoffsets", "default")
                parameter("levels", "default")
            }.body()
            Log.d(TAG, response.decodeURLPart())
            val weatherData = FrostData.fromJson(response)
            Log.i(TAG, "Successfully parsed ${weatherData.size} weather data points")
            weatherData
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching weather data: ${e.message}", e)
            emptyList()
        }
    }

    @Serializable
    private data class SourceResponse(val data: List<Source>)
    @Serializable
    private data class Source(val id: String)
}