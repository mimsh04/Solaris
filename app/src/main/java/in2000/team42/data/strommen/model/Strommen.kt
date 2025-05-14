package in2000.team42.data.strommen.model

import kotlinx.serialization.Serializable


@Serializable
data class Strommen(
    val NOK_per_kWh:Double,
    val EUR_per_kWh:Double,
    val EXR:Double,  //Exchange rate for NOK to EUR
    val time_start:String, // Start- and end time for price (ISO 8601-format)
    val time_end:String,
)
