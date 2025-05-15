package in2000.team42.data.productionCalculation

import in2000.team42.data.pgvis.model.KwhMonthlyResponse
import in2000.team42.data.productionCalculation.model.ProductionCalculation
import in2000.team42.ui.screens.home.DisplayWeather



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

private fun parseSnowFromMm(snow: String) =
    snow.split("m").first().replace(",", ".").toDouble()

private fun parseCloudFromPercent(cloud: String) =
    cloud.split("%").first().replace(",", ".").toDouble()

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
    kwhMonthlyData.forEachIndexed {i,  monthlyData ->
        val (_, snowKwhLoss, _) = calculateSnowImpact(monthlyData.averageMonthly,
                if (sortedWeatherData[i].snow == "ukjent")
                0.0 else
                parseSnowFromMm(sortedWeatherData[i].snow)
        )
        val (_, cloudKwhLoss, _) = calculateCloudImpact(monthlyData.averageMonthly,
                if (sortedWeatherData[i].cloud == "ukjent")
                60.0 else
                parseCloudFromPercent(sortedWeatherData[i].cloud)
        )
        monthlyProductionCalculations.add(ProductionCalculation(
            kWhPotential = monthlyData.averageMonthly,
            kWhAfterCalculation = monthlyData.averageMonthly - (snowKwhLoss + cloudKwhLoss),
            snoTap = snowKwhLoss,
            skyTap = cloudKwhLoss,
            month = monthlyData.month
        ))
    }
    return monthlyProductionCalculations
}