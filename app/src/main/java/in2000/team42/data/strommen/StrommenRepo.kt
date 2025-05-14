package in2000.team42.data.strommen

import android.util.Log
import in2000.team42.data.strommen.model.Strommen


class StrommenRepo {
    private val dataSource = StrommenDataSource()
    /**
     * Fetches price for a specific date and specific area.
     * @param year Year for request
     * @param month Month (01-12)
     * @param day Day (01-31)
     * @param area Area (f.eks. NO1, NO2)
     * @return Result with a list of prices, or error if the request fails
     */
    suspend fun getStromPriser(year: Int, month: Int, day: Int, area: String): Result<List<Strommen>> {
        return try {
            val prices = dataSource.getStromInfo(year, month, day, area)
            Log.d("REPOSITORY", "Hentet ${prices.size} priser: $prices")
            if (prices.isNotEmpty()) {
                Result.success(prices)
            } else {
                Result.failure(Exception("Ingen data tilgjengelig"))
            }
        } catch (e: Exception) {
            Log.e("REPOSITORY", "Feil ved henting: ${e.message}")
            Result.failure(e)
        }
    }
}