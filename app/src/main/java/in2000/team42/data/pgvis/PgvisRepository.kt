package in2000.team42.data.pgvis

import in2000.team42.data.pgvis.model.DailyProfile
import in2000.team42.data.pgvis.model.KwhMonthlyResponse

class PgvisRepository (private val pgvisDatasource: PgvisDatasource) {
    suspend fun getRadiationData(
        lat: Double,
        lon: Double,
        month: Int,
        incline: Float,
        retning: Float
    ): List<DailyProfile> {
        return pgvisDatasource.getDailyRadiation(lat, lon, month, incline, retning)
    }

    suspend fun getMonthlyKwh(
        lat: Double,
        lon: Double,
        incline: Float,
        retning: Float = 0f,
        peakPower: Float,
        pvTech: PvTech,
    ): List<KwhMonthlyResponse.MonthlyKwhData> {
        return pgvisDatasource.getMonthlyKwh(lat, lon, incline, retning, peakPower, pvTech)
    }
}