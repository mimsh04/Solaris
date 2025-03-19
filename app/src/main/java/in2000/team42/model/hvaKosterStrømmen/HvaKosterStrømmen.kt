package in2000.team42.model.hvaKosterStrømmen

data class HvaKosterStrømmen(

    //tid start og slutt er i ISO 8601 format som senere må konverteres
    //til faktisk dato/tid med OffsetDataTime

    val NOK_per_kWh:Double,
    val EUR_per_kWh:Double,
    val EXR:Double,
    val time_start:String,
    val time_end:String


)
