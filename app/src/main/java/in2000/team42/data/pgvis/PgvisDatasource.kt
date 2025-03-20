package in2000.team42.data.pgvis

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.serialization.kotlinx.json.*
import io.ktor.client.plugins.contentnegotiation.*

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
     */
    suspend fun getDailyRadiation(
        lat: Float,
        lon: Float,
        month: Int,
        incline: Float,
        retning: Float,
    ) {
        val url = "https://re.jrc.ec.europa.eu/api/v5_3/DRcalc?lat=$lat&lon=$lon&outputformat=json&month=$month&angle=$incline&aspect=$retning&global=1"

    }
}