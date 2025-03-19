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
     * Hent daglig radiasjon for en gitt måned
     *
     * @param lat Latitude
     * @param lon Longitude
     * @param month Måned (1 - for januar, 12 - for desember, 0 for alle)
     */
    suspend fun getDailyRadiation(lat: Float, lon: Float, month: Int) {

    }
}