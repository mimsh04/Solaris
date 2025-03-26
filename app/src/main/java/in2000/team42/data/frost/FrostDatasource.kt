package in2000.team42.data.frost

import android.util.Log
import in2000.team42.data.frost.model.FrostData
import in2000.team42.data.frost.model.SourceResponse
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
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
    suspend fun fetchFrostDataByCoords(latitude: Double, longitude: Double): FrostData? = withContext(Dispatchers.IO) {
        val params = mapOf(
            "sources" to "nearest(POINT($longitude $latitude))",
            "elements" to "air_temperature,accumulated(liquid_water_content_of_surface_snow),cloud_area_fraction",
            "referencetime" to "now-PT24H/now"
        )

        Log.v(TAG, "Starting API request with params: $params")

        try {
            val response = client.get("observations/v0.jsonld") {
                params.forEach { (key, value) ->
                    parameter(key, value)  // Add query parameters
                }
            }  // Deserialize til FrostResponse
            Log.i(TAG, "Response received successfully")
            Log.d(TAG, "Raw response: $response")

            null
        } catch (e: Exception) {
            Log.e(TAG, "API request failed: ${e.message}", e)
            null
        }
    }


    //https://frost.met.no/sources/v0.jsonld?types=SensorSystem&geometry=nearest(POINT(10.5555 59))
    suspend fun fetchNearestStation(latitude: Double, longitude: Double): String{
        val params = mapOf(
            "types" to "SensorSystem",
            "geometry" to "nearest(POINT($longitude $latitude))"
        )
        return try{
            val response = client.get("source/v0.jsonld"){
                params.forEach { (key, value) ->
                    parameter(key, value)
                }
            }.body<SourceResponse>()
            Log.i(TAG, "Response received successfully")
            Log.d(TAG, "Raw response: $response")
            response.data[0].id

        }catch(e: Exception) {
            Log.e(TAG,"API request failed: ${e.message}", e)
            ""
        }

    }
}