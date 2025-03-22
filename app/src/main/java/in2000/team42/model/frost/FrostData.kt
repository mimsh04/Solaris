package in2000.team42.model.frost

data class FrostData(
    val temperature: Double?,          // °C
    val snowWaterEquivalent: Double?,  // kg/m²
    val cloudCoverage: Int?            // %
)