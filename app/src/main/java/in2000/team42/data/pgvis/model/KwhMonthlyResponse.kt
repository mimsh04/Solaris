package in2000.team42.data.pgvis.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

abstract class KwhMonthlyResponse {
    @Serializable
    data class Response(
        val outputs: Outputs
    )

    @Serializable
    data class Outputs(
        val monthly: Monthly
    )

    @Serializable
    data class Monthly(
        val fixed: List<MonthlyKwhData>
    )

    @Serializable
    data class MonthlyKwhData(
        val month: Int,
        @SerialName("E_d") val avarageDaily: Double,
        @SerialName("E_m")val averageMonthly: Double,
        @SerialName("H(i)_d") val averageDailyRadiation: Double,
        @SerialName("H(i)_m") val averageMonhtlyRadiation: Double,
        @SerialName("SD_m") val yearlyVariation: Double
    )
}

