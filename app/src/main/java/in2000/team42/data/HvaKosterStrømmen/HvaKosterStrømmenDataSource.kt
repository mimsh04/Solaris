package in2000.team42.data.HvaKosterStrømmen


import android.util.Log
import in2000.team42.model.hvaKosterStrømmen.HvaKosterStrømmen
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
//import io.ktor.serialization.kotlinx.json.*
//import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.get
import kotlinx.serialization.json.Json

class HvaKosterStrømmenDataSource {

//    private val ktorHttpclient=HttpClient(CIO){
//        install(ContentNegotiation){
//            json(Json {
//                ignoreUnknownKeys= true
//                isLenient = true
//            })
//        }
//    }


    private val baseUrl="https://www.hvakosterstrommen.no/api/v1/prices/%d/%02d-%02d_%s.json"

    suspend fun getStrømInfo(year:Int,month:Int,day:Int,area:String):List<HvaKosterStrømmen>{
        val formattedMonth = String.format("%02d", month)
        val formattedDay = String.format("%02d",day)
        val url = String.format(baseUrl,year,month,day,area)
//        return ktorHttpclient.get(url).body()
    }
}
