package in2000.team42.data.pgvis

import android.util.Log
import in2000.team42.data.pgvis.model.DailyProfile
import in2000.team42.data.pgvis.model.RadiationResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.serialization.kotlinx.json.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.get

class PgvisDatasource {
    private val ktorHttpClient = HttpClient(CIO) {
        install(ContentNegotiation) {
            json()
        }
    }

    /**
     * Hent daglig gjenosnnitlig radiasjon for hver time av dagen for en gitt måned
     *
     * @param lat Latitude
     * @param lon Longitude
     * @param month Måned (1 - for januar, 2 - for febraur osv., 0 for alle)
     * @param incline Hellingsvinkel (i grader)
     * @param retning Retning (i grader)
     *
     * @return Liste av DailyProfile
     */
    suspend fun getDailyRadiation(
        lat: Double,
        lon: Double,
        month: Int,
        incline: Float,
        retning: Float,
    ): List<DailyProfile> {
        val url = "https://re.jrc.ec.europa.eu/api/v5_3/DRcalc?lat=$lat&lon=$lon&outputformat=json&month=$month&angle=$incline&aspect=$retning&global=1"
        return try {
            val response: RadiationResponse = ktorHttpClient.get(url).body()
            response.outputs?.daily_profile ?: emptyList()
        } catch (e: Exception) {
            println("Error fetching radiation data: ${e.message}")
            emptyList()
        }
    }
}