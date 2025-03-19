package in2000.team42.data.HvaKosterStrømmen

import android.util.Log
import in2000.team42.model.hvaKosterStrømmen.HvaKosterStrømmen
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.serialization.kotlinx.json.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.get

class HvaKosterStrømmenDataSource {

    private val ktorHttpclient = HttpClient(CIO) {
        install(ContentNegotiation) {
            json()
        }
    }



    suspend fun getStrømInfo(
        year: Int.Companion,
        month: Int.Companion,
        day: Int.Companion,
        area: String
    ): List<HvaKosterStrømmen> {

        val formatMonth="%02d".format(month)
        val formatDay="%02d".format(day)


        val baseUrl = "https://www.hvakosterstrommen.no/api/v1/prices/$year/$formatMonth-$formatDay"+ "_$area.json"


        return try {
            val response: List<HvaKosterStrømmen> = ktorHttpclient.get(baseUrl).body()
            response
        } catch (e: Exception) {
            Log.d("HVAKOSTERSTRØMMENDATASOURCE", "Feil ved henting av strøminfo:${e.message}")
            emptyList()
        }
    }


}