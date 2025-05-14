package in2000.team42.data.productionCalculation.model

data class ProductionCalculation(
    val kWhPotential: Double,
    val kWhEtterUtregning: Double,
    val snoTap: Double,
    val skyTap: Double,
    val month: Int,
)