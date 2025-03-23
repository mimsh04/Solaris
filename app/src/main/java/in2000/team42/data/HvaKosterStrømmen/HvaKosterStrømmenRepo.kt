package in2000.team42.data.HvaKosterStrømmen

import android.util.Log
import in2000.team42.model.hvaKosterStrømmen.HvaKosterStrømmen
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class HvaKosterStrømmenRepo {
    private val dataSource = HvaKosterStrømmenDataSource()

    // Parse ISO 8601 date string to Date object
    fun parseTime(isoDateTime: String): Date {
        val isoFormatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX", Locale.getDefault())
        isoFormatter.timeZone = TimeZone.getTimeZone("UTC")
        return isoFormatter.parse(isoDateTime) ?: throw IllegalArgumentException("Invalid date format: $isoDateTime")
    }

    suspend fun getStrømPriser(year: Int, month: Int, day: Int, area: String): Result<List<HvaKosterStrømmen>> {
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