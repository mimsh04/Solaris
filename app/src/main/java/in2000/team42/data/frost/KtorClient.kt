package in2000.team42.data.frost

import android.util.Base64
import android.util.Log
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.defaultRequest
//import io.ktor.client.plugins.logging.*
import io.ktor.client.request.header
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

object KtorClient {
    private const val BASE_URL = "https://frost.met.no/"
    private const val CLIENT_ID = "5fa50311-61ee-4aa0-8f29-2262c21212e5"
    private const val TAG = "KtorClient"

    val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json {
                isLenient = true
                ignoreUnknownKeys = true
            })
        }
        /*install(Logging) {  // Valgrfitt: lagt til Log requests/responses
            logger = object : Logger {
                override fun log(message: String) {
                    Log.d(TAG, message)
                }
            }
            level = LogLevel.BODY
        }*/
        defaultRequest {
            val authToken = Base64.encodeToString("$CLIENT_ID:".toByteArray(), Base64.NO_WRAP)
            header("Authorization", "Basic $authToken")  // Basic Auth header
            url(BASE_URL)
        }
    }
}