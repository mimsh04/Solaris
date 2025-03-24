package in2000.team42.data.frost

import android.content.Context
import android.util.Log
import in2000.team42.model.frost.FrostData
import in2000.team42.model.frost.FrostResponse
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class FrostDataSource(private val context: Context) {

    private val TAG = "FrostDataSource"
    private val client = KtorClient.client

    /**
     * Henter daglig temperatur, skydekke og snømengde for de siste 24 timene når funksjonen blir kalt
     * hvis det har blitt gjort målinger for hver time. Kan hende en stasjon ikke måler hver time
     * eller ikke har utstyret for å målet en type data.
     *
     * @param latitude Latitude
     * @param longitude Longitude
     *
     * @return Klasse med temperatur, skydekke og snø (eller mengden snø ekvivalent med vann på bakken)
     */
    suspend fun fetchFrostDataByCoords(latitude: Double, longitude: Double): FrostData? = withContext(Dispatchers.IO) {
        val params = mapOf(
            "sources" to "nearest(POINT($longitude $latitude))",
            "elements" to "air_temperature,accumulated(liquid_water_content_of_surface_snow),cloud_area_fraction",
            "referencetime" to "now-PT24H/now"
        )

        Log.v(TAG, "Starting API request with params: $params")

        try {
            val response: FrostResponse = client.get("observations/v0.jsonld") {
                params.forEach { (key, value) ->
                    parameter(key, value)  // Add query parameters
                }
            }.body()  // Deserialize til FrostResponse
            Log.i(TAG, "Response received successfully")
            Log.d(TAG, "Raw response: $response")

            parseResponse(response)
        } catch (e: Exception) {
            Log.e(TAG, "API request failed: ${e.message}", e)
            null
        }
    }

    private fun parseResponse(response: FrostResponse): FrostData? {
        return try {
            Log.v(TAG, "Starting response parsing")
            val observations = response.data.firstOrNull()?.observations ?: return null

            var temp: Double? = null
            var snow: Double? = null
            var clouds: Int? = null

            observations.forEach { obs ->
                when (obs.elementId) {
                    "air_temperature" -> {
                        temp = obs.value
                        Log.d(TAG, "Parsed air_temperature: $temp")
                    }
                    "accumulated(liquid_water_content_of_surface_snow)" -> {
                        snow = obs.value
                        Log.d(TAG, "Parsed snow_water_equivalent: $snow")
                    }
                    "cloud_area_fraction" -> {
                        clouds = obs.value.toInt()
                        Log.d(TAG, "Parsed cloud_area_fraction: $clouds")
                    }
                }
            }

            val weatherData = FrostData(temp, snow, clouds) // Lager data objektet
            Log.v(TAG, "Parsing completed successfully")
            weatherData
        } catch (e: Exception) {
            Log.e(TAG, "Error parsing response: ${e.message}", e)
            null
        }
    }
}