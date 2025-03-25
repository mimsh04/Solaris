package in2000.team42.model.strommen

import kotlinx.serialization.Serializable


@Serializable
data class Strommen(
    val NOK_per_kWh:Double,
    val EUR_per_kWh:Double,
    val EXR:Double,  //valutakurs for NOK til EUR
    val time_start:String, // Start- og end tidspunkt for prisen (ISO 8601-format)
    val time_end:String,
)
