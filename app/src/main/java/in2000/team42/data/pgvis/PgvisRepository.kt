package in2000.team42.data.pgvis

import in2000.team42.data.pgvis.model.DailyProfile

class PgvisRepository (private val pgvisDatasource: PgvisDatasource) {
    suspend fun getRadiationData(
        lat: Float,
        lon: Float,
        month: Int,
        incline: Float,
        retning: Float
    ): List<DailyProfile> {
        return pgvisDatasource.getDailyRadiation(lat, lon, month, incline, retning)
    }
}