package in2000.team42.data.productionCalculation

import android.util.Log
import in2000.team42.data.pgvis.model.KwhMonthlyResponse
import in2000.team42.ui.screens.home.DisplayWeather

data class ProductionCalculation(
    val kWhPotential: Double,
    val kWhEtterUtregning: Double,
    val snoTap: Double,
    val skyTap: Double,
    val month: Int,
)

private val monthNameMap = mapOf(
    "Jan" to 1, "Feb" to 2, "Mar" to 3, "Apr" to 4, "May" to 5, "Jun" to 6,
    "Jul" to 7, "Aug" to 8, "Sep" to 9, "Oct" to 10, "Nov" to 11, "Dec" to 12
)


private fun calculateSnowImpact(
    kWhPotential: Double, dailySnowfallMm: Double
): Triple<Double, Double, Double> {
    // Constant for loss per mm of daily snowfall
    val lossCoefficient = 0.02001

    // Calculate percentage loss
    val percentageLoss = 2.001 * dailySnowfallMm

    // Calculate fractional loss
    val fractionalLoss = lossCoefficient * dailySnowfallMm

    // Calculate kWh lost
    val kWhLost = fractionalLoss * kWhPotential

    // Calculate actual kWh produced
    val kWhActual = kWhPotential * (1 - fractionalLoss)

    return Triple(percentageLoss, kWhLost, kWhActual)
}

private fun calculateCloudImpact(
    kWhPotential: Double, cloudCoveragePercent: Double
): Triple<Double, Double, Double> {
    // Constant for loss per 1% cloud coverage
    val lossCoefficient = 0.003

    // Calculate percentage loss
    val percentageLoss = 0.8 * cloudCoveragePercent

    // Calculate fractional loss
    val fractionalLoss = lossCoefficient * cloudCoveragePercent

    // Calculate kWh lost
    val kWhLost = fractionalLoss * kWhPotential

    // Calculate actual kWh produced
    val kWhActual = kWhPotential * (1 - fractionalLoss)

    return Triple(percentageLoss, kWhLost, kWhActual)
}

private fun sortWeatherDataByMonth(weatherData: List<DisplayWeather>): List<DisplayWeather> {
    return weatherData.sortedBy { displayWeather ->
        val monthStr = displayWeather.month.split(" ").firstOrNull()
        monthNameMap[monthStr] ?: 0
    }
}

fun calculateWithCoverage(
    kwhMonthlyData: List<KwhMonthlyResponse.MonthlyKwhData>,
    weatherData: List<DisplayWeather>
) : List<ProductionCalculation>{
    val sortedWeatherData = sortWeatherDataByMonth(weatherData)
    val monthlyProductionCalculations = mutableListOf<ProductionCalculation>()
    kwhMonthlyData.forEachIndexed() {i,  monthlyData ->
        val (_, snowKwhLoss, _) = calculateSnowImpact(monthlyData.averageMonthly,
            if (sortedWeatherData[i].snow == "Ukjent") 0.0 else
                sortedWeatherData[i].snow.split("m").first().toDouble())
        val (_, cloudKwhLoss, _) = calculateCloudImpact(monthlyData.averageMonthly,
            if (sortedWeatherData[i].cloud == "Ukjent") 60.0 else
                sortedWeatherData[i].cloud.split("%").first().toDouble())

        monthlyProductionCalculations.add(ProductionCalculation(
            kWhPotential = monthlyData.averageMonthly,
            kWhEtterUtregning = monthlyData.averageMonthly - (snowKwhLoss + cloudKwhLoss),
            snoTap = snowKwhLoss,
            skyTap = cloudKwhLoss,
            month = monthlyData.month
        ))
    }
    Log.d("ProduksjonKalkulering", "ProduksjonKalkuleringList: $monthlyProductionCalculations")
    return monthlyProductionCalculations
}