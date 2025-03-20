package in2000.team42.data.HvaKosterStrømmen

import in2000.team42.model.hvaKosterStrømmen.HvaKosterStrømmen

class HvaKosterStrømmenRepo {

    private val strømmenDataSource=HvaKosterStrømmenDataSource()


    suspend fun getStromPriser(year :Int, month : Int, day : Int, area: String = toString()):List<HvaKosterStrømmen>{
       return strømmenDataSource.getStromInfo(year = year, month = month, day = day, area = area)

    }



}