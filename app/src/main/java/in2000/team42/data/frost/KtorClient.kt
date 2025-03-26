package in2000.team42.data.frost

import android.net.Credentials
import android.util.Base64
import android.util.Log
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BasicAuthCredentials
import io.ktor.client.plugins.auth.providers.basic
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.basicAuth
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
            json()
            ignoreUnknownKeys = true
        }
        install(Auth) {
            basic {
                credentials {
                    BasicAuthCredentials(username = "5fa50311-61ee-4aa0-8f29-2262c21212e5", password = "")
                }

            }
        }
    }
}