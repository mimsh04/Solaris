package in2000.team42.data.strommen

import android.util.Log
import in2000.team42.data.strommen.model.Strommen
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.serialization.kotlinx.json.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import kotlinx.serialization.json.Json
import java.util.Locale

class StrommenDataSource {

    private val ktorHttpClient = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true
            })
        }
    }
    private val baseUrl= "https://www.hvakosterstrommen.no/api/v1/prices/%d/%s-%s_%s.json"


    /**
     * Henter strømprisdata fra API-et.
     * @param year Året for forespørselen
     * @param month Måneden (01-12)
     * @param day Dagen (01-31)
     * @param area Strømprisområde (f.eks. NO1, NO2)
     * @return En liste med strømpriser for den angitte datoen og området.
     */
    suspend fun getStromInfo(year: Int, month: Int, day: Int, area: String): List<Strommen> {
        val formattedMonth = String.format(Locale.US, "%02d", month)
        val formattedDay = String.format(Locale.US, "%02d", day)
        val url = String.format(Locale.US, baseUrl, year, formattedMonth, formattedDay, area)
        Log.d("DATASOURCE", "Requesting URL: $url")
        try {
            val response = ktorHttpClient.get(url)
            Log.d("DATASOURCE", "Response: ${response.status}, ${response.bodyAsText()}")
            return response.body()
        } catch (e: Exception) {
            Log.e("DATASOURCE", "Fetch failed: ${e.message}")
            throw e
        }
    }
}