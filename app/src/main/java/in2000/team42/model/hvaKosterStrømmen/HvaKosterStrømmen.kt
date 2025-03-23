package in2000.team42.model.hvaKosterStrømmen

import kotlinx.serialization.Serializable


@Serializable
data class HvaKosterStrømmen(
    val NOK_per_kWh:Double,
    val EUR_per_kWh:Double,
    val EXR:Double,
    val time_start:String,
    val time_end:String,
)
