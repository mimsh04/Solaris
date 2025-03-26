package in2000.team42.data.frost

import android.util.Log
import in2000.team42.data.frost.model.FrostData
import in2000.team42.data.frost.model.FrostResponse
import in2000.team42.data.frost.model.SourceResponse
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.statement.bodyAsText
import io.ktor.http.encodeURLParameter
import io.ktor.http.isSuccess
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class FrostDatasource() {

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
    suspend fun getFrostData(lat: Double, lon: Double): FrostData? = withContext(Dispatchers.IO) {
        val stationId = getNearestStation(lat, lon)
        if (stationId.isNullOrEmpty()) {
            Log.e(TAG, "Failed to fetch station ID for coordinates ($lon, $lat)")
            return@withContext null
        }

        val url = "https://frost.met.no/observations/v0.jsonld?sources=$stationId&elements=air_temperature,accumulated(liquid_water_content_of_surface_snow),cloud_area_fraction&referencetime=latest-PT24H"
        return@withContext try {
            val response: FrostResponse = client.get(url).body()
            Log.d(TAG, "Weather response: $response")
            parseResponse(response)
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching weather data: ${e.message}", e)
            null
        }
    }

    //https://frost.met.no/sources/v0.jsonld?types=SensorSystem&geometry=nearest(POINT(10.5555 59))
    suspend fun getNearestStation(lat: Double, lon: Double): String? {
        val url = "https://frost.met.no/sources/v0.jsonld?types=SensorSystem&geometry=nearest(POINT($lon $lat))"
        return try {
            val response: SourceResponse = client.get(url).body()
            Log.d(TAG, "Station response: $response")
            response.data.firstOrNull()?.id ?: run {
                Log.w(TAG, "No stations found for coordinates ($lon, $lat)")
                null
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching nearest station: ${e.message}", e)
            null
        }
    }

    private fun parseResponse(response: FrostResponse): FrostData? {
        return try {
            val observations = response.data.firstOrNull()?.observations ?: return null
            var temp: Double? = null
            var snow: Double? = null
            var clouds: Int? = null

            observations.forEach { obs ->
                when (obs.elementId) {
                    "air_temperature" -> temp = obs.value
                    "accumulated(liquid_water_content_of_surface_snow)" -> snow = obs.value
                    "cloud_area_fraction" -> clouds = obs.value.toInt()
                }
            }

            FrostData(temp, snow, clouds)
        } catch (e: Exception) {
            Log.e(TAG, "Error parsing response: ${e.message}", e)
            null
        }
    }}