package in2000.team42.data.pgvis

import android.util.Log
import in2000.team42.data.pgvis.model.DailyProfile
import in2000.team42.data.pgvis.model.KwhMonthlyResponse
import in2000.team42.data.pgvis.model.RadiationResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.serialization.kotlinx.json.json

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
     * Fetch daily average radiation for each hour of the day for a given month
     *
     * @param latitude Latitude
     * @param longitude Longitude
     * @param month Month (1 - for January, 2 - for February, etc., 0 for all)
     * @param tilt Tilt angle (in degrees)
     * @param direction Direction (in degrees)
     *
     * @return List of DailyProfile
     */
    suspend fun getDailyRadiation(
        latitude: Double,
        longitude: Double,
        month: Int,
        tilt: Float,
        direction: Float,
    ): List<DailyProfile> {
        val url = "https://re.jrc.ec.europa.eu/api/v5_3/DRcalc?lat=$latitude&lon=$longitude&outputformat=json&month=$month&angle=$tilt&aspect=$direction&global=1"
        return try {
            val response: RadiationResponse = ktorHttpClient.get(url).body()
            response.outputs?.daily_profile ?: emptyList()
        } catch (e: Exception) {
            Log.e("PGVIS", "Error fetching radiation data: ${e.message}")
            emptyList()
        }
    }

    /**
     * Fetch monthly average kWh for each month between the years 2003 - 2023
     *
     * @param latitude Latitude
     * @param longitude Longitude
     * @param tilt Tilt angle (in degrees)
     * @param direction Direction (in degrees)
     * @param peakPower Peak power (in kW, how much the panels can produce)
     * Learn more here: [PVGIS FAQ](https://joint-research-centre.ec.europa.eu/photovoltaic-geographical-information-system-pvgis/getting-started-pvgis/using-pvgis-frequently-asked-questions_en)
     * @param pvTech PV technology
     *
     * @return List of MonthlyKwhData
     */
    suspend fun getMonthlyKwh(
        latitude: Double,
        longitude: Double,
        tilt: Float,
        direction: Float = 0f,
        peakPower: Float,
        pvTech: PvTech,
    ): List<KwhMonthlyResponse.MonthlyKwhData> {
        val url = "https://re.jrc.ec.europa.eu/api/v5_3/PVcalc?outputformat=json" +
            "&lat=$latitude" +
            "&lon=$longitude" +
            "&raddatabase=PVGIS-ERA5" +
            "&peakpower=$peakPower" +
            "&loss=14" +
            "&mountingplace=building" +
            "&pvtechchoice=${pvTech.value}" +
            "&angle=$tilt" +
            "&aspect=$direction"
        return try {
            val response = ktorHttpClient.get(url).body<KwhMonthlyResponse.Response>()
            response.outputs.monthly.fixed
        } catch (e: Exception) {
            Log.e("PGVIS", "Error fetching kWh production data: ${e.message}")
            emptyList()
        }
    }
}
