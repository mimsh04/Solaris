package in2000.team42.data.HvaKosterStrømmen

import android.content.ContentValues.TAG
import android.util.Log
import in2000.team42.model.hvaKosterStrømmen.HvaKosterStrømmen

class HvaKosterStrømmenRepo {

    private val dataSource = HvaKosterStrømmenDataSource()

    suspend fun getStrømPriser(
        year :Int,
        month : Int,
        day : Int,
        area: String
    ): Result<List<HvaKosterStrømmen>>{
      return try {
          val prices = dataSource.getStromInfo(year, month, day, area)
          Log.d(TAG, "Retrieved ${prices.size} prices")
          if (prices.isNotEmpty()){
              Result.success(prices)
          } else {
              Result.failure(Exception("Ingen data tilgjengelig"))
          }
      } catch (e: Exception) {
          Log.e(TAG, "Fetch error: ${e.message}", e)
          Result.failure(e)
      }
    }
}