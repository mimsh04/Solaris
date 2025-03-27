package in2000.team42.data.strommen

import android.util.Log
import in2000.team42.model.strommen.Strommen


class StrommenRepo {
    private val dataSource = StrommenDataSource()
    /**
     * Henter strømprisene for en spesifikk dato og et spesifikt område.
     * @param year Året for forespørselen
     * @param month Måneden (01-12)
     * @param day Dagen (01-31)
     * @param area Strømprisområde (f.eks. NO1, NO2)
     * @return Resultat med en liste over strømpriser, eller en feil hvis henting mislykkes
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