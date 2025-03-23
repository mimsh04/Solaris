package in2000.team42.data.HvaKosterStrømmen

import android.util.Log
import in2000.team42.model.hvaKosterStrømmen.HvaKosterStrømmen

class HvaKosterStrømmenRepo {

    private val dataSource = HvaKosterStrømmenDataSource()


    suspend fun getStrømPriser(year :Int, month : Int, day : Int, area: String ): Result<List<HvaKosterStrømmen>>{
      return try {
          val prices = dataSource.getStromInfo(year, month, day, area)
          if (prices.isNotEmpty()){
              Log.d("REPOSITORY", "Hentet ${prices.size} priser for $year-$month-$day i $area")
              Result.success(prices)
          } else {
              Log.d("REPOSITORY", "Ingen priser hentet for $year-$month-$day i $area")
              Result.failure(Exception("Ingen data tilgjengelig"))
          }
      } catch (e: Exception) {
          Log.e("REPOSITORY", "Feil ved henting av strømpriser: ${e.message}")
          Result.failure(e)
      }
    }
}