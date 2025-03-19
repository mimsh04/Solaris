package in2000.team42.data.pgvis

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.serialization.kotlinx.json.*

class PgvisDatasource {
    private val ktorHttpClient = HttpClient(CIO) {
        install(ContentNegtionation)
    }
}