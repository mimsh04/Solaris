package data

import android.util.Log // Added import for Logcat
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.auth.*
import io.ktor.client.plugins.auth.providers.*
import io.ktor.client.request.*
import io.ktor.http.*
import models.FrostData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable

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
                parameter("nearestmaxcount", 1)
            }.body()
            Log.v(TAG, "Received response from Frost API: $response")

            val json = kotlinx.serialization.json.Json { ignoreUnknownKeys = true }
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
    suspend fun getWeatherData(stationId: String, referenceTime: String): List<FrostData> = withContext(Dispatchers.IO) {
        val url = "$baseUrl/observations/v0.jsonld"
        Log.d(TAG, "Fetching weather data for station $stationId at time $referenceTime")
        try {
            val response: String = client.get(url) {
                parameter("sources", stationId)
                parameter("referencetime", referenceTime)
                parameter("elements", "air_temperature,sum(precipitation_amount P1D),cloud_area_fraction")
            }.body()
            Log.v(TAG, "Received weather data response: $response")

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