package in2000.team42.data.frost

import android.content.Context
import com.android.volley.AuthFailureError
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import in2000.team42.model.frost.FrostData
import org.json.JSONObject
import android.util.Base64
import android.util.Log
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class FrostDataSource(private val context: Context) {

    private val clientId = "5fa50311-61ee-4aa0-8f29-2262c21212e5"
    private val TAG = "FrostDataSource" // Variabel for å enklere skrive logcat

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

    suspend fun fetchFrostDataByCoords(latitude: Double, longitude: Double): FrostData? = suspendCoroutine { continuation ->
        val url = "https://frost.met.no/observations/v0.jsonld?" +
                "sources=nearest(POINT($longitude $latitude))" + // Eksempel stasjon: sources=SN18700
                "&elements=air_temperature,accumulated(liquid_water_content_of_surface_snow),cloud_area_fraction" +
                "&referencetime=now-PT24H/now"

        Log.v(TAG, "Starting API request to: $url")

        val queue = Volley.newRequestQueue(context)

        val request = object : StringRequest(
            Method.GET, url,
            { response ->
                Log.i(TAG, "Response received successfully")
                Log.d(TAG, "Raw response: $response")
                val data = parseResponse(response)
                if (data != null) {
                    Log.i(TAG, "Data parsed: Temp=${data.temperature}, Snow=${data.snowWaterEquivalent}, Clouds=${data.cloudCoverage}")
                } else {
                    Log.w(TAG, "Failed to parse response data") // Warn: Parsing returned null
                }

                continuation.resume(data)
            },
            { error ->
                Log.e(TAG, "API request failed: ${error.message}", error)
                continuation.resume(null)
            }
        ) {
            // Passer på autorisasjon under API kall
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val headers = HashMap<String, String>()
                val auth = Base64.encodeToString("$clientId:".toByteArray(), Base64.NO_WRAP)
                headers["Authorization"] = "Basic $auth"
                Log.d(TAG, "Authorization header set: Basic $auth")

                return headers
            }
        }
        Log.i(TAG, "Adding request to Volley queue")
        queue.add(request)
    }

    private fun parseResponse(response: String): FrostData? {
        return try {
            Log.v(TAG, "Starting JSON parsing")
            val json = JSONObject(response)
            val dataArray = json.getJSONArray("data")
            val observation = dataArray.getJSONObject(0)
            val observationsArray = observation.getJSONArray("observations")

            var temp: Double? = null
            var snow: Double? = null
            var clouds: Int? = null

            for (i in 0 until observationsArray.length()) {
                val obs = observationsArray.getJSONObject(i)
                when (obs.getString("elementId")) {
                    "air_temperature" -> {
                        temp = obs.getDouble("value")
                        Log.d(TAG, "Parsed air_temperature: $temp")
                    }
                    "accumulated(liquid_water_content_of_surface_snow)" -> {
                        snow = obs.getDouble("value")
                        Log.d(TAG, "Parsed snow_water_equivalent: $snow")
                    }
                    "cloud_area_fraction" -> {
                        clouds = obs.getInt("value")
                        Log.d(TAG, "Parsed cloud_area_fraction: $clouds")
                    }
                }
            }

            val weatherData = FrostData(temp, snow, clouds)
            Log.v(TAG, "Parsing completed successfully")
            weatherData
        } catch (e: Exception) {
            Log.e(TAG, "Error parsing JSON: ${e.message}", e)
            null
        }
    }
}