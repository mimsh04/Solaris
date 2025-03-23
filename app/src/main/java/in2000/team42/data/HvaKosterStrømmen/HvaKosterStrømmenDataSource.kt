package in2000.team42.data.HvaKosterStrømmen

import android.util.Log
import in2000.team42.model.hvaKosterStrømmen.HvaKosterStrømmen
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.serialization.kotlinx.json.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import kotlinx.serialization.json.Json

class HvaKosterStrømmenDataSource {

    private val ktorHttpClient = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true
            })
        }
    }


    private val baseUrl="https://www.hvakosterstrommen.no/api/v1/prices/%d/%02d-%02d_%s.json"

    suspend fun getStromInfo(year: Int, month: Int, day: Int, area: String): List<HvaKosterStrømmen> {
        val formattedMonth = String.format("%02d", month)
        val formattedDay = String.format("%02d", day)
        val url = String.format(baseUrl, year, formattedMonth, formattedDay, area)
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