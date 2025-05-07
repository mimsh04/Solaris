package in2000.team42.data.pgvis

import in2000.team42.data.pgvis.model.DailyProfile
import in2000.team42.data.pgvis.model.KwhMonthlyResponse
import in2000.team42.data.pgvis.model.RadiationResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.serialization.kotlinx.json.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.get

enum class PvTech(val value: String) {
    CRYST_SI("crystSi"),
    CIS("CIS"),
    CD_TE("CdTe"),
    UNKNOWN("Unknown")
}

class PgvisDatasource {
    private val ktorHttpClient = HttpClient(CIO) {
        install(ContentNegotiation) {
            json (
                kotlinx.serialization.json.Json {
                    ignoreUnknownKeys = true
                }
            )
        }
    }

    /**
     * Hent daglig gjennomsnittlig radiasjon for hver time av dagen for en gitt måned
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



    /**
     * Henter månedlig gjennomsnittlig kWh for hver måned mellem årene 2003 - 2023
     *
     * @param lat Latitude
     * @param lon Longitude
     * @param incline Hellingsvinkel (i grader)
     * @param retning Retning (i grader)
     * @param peakPower Peak power (i kW, hvor mye kan panelene produsere)
     * Les mer her: [PVGIS FAQ](https://joint-research-centre.ec.europa.eu/photovoltaic-geographical-information-system-pvgis/getting-started-pvgis/using-pvgis-frequently-asked-questions_en)
     * @param pvTech PV teknologi
     *
     * @return Liste av MonthlyKwhData
     */
    suspend fun getMonthlyKwh(
        lat: Double,
        lon: Double,
        incline: Float,
        retning: Float = 0f,
        peakPower: Float,
        pvTech: PvTech,

    ) :List<KwhMonthlyResponse.MonthlyKwhData> {
        val url = "https://re.jrc.ec.europa.eu/api/v5_3/PVcalc?outputformat=json" +
            "&lat=$lat" +
            "&lon=$lon" +
            "&raddatabase=PVGIS-SARAH3" +
            "&peakpower=$peakPower" +
            "&loss=14" +
            "&mountingplace=building" +
            "&pvtechchoice=${pvTech.value}" +
            "&angle=$incline" +
            "&aspect=$retning"
        return try {
            val response = ktorHttpClient.get(url).body<KwhMonthlyResponse.Response>()
            response.outputs.monthly.fixed
        } catch (e: Exception) {
            println("Error fetching radiation data: ${e.message}")
            emptyList()
        }
    }
}