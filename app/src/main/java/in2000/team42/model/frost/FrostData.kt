package in2000.team42.model.frost

data class FrostData(
    val temperature: Double?,          // °C
    val snowWaterEquivalent: Double?,  // kg/m²
    val cloudCoverage: Int?            // %
)

data class FrostResponse(
    val data: List<ObservationData>
)

data class ObservationData(
    val observations: List<Observation>
)

data class Observation(
    val elementId: String,
    val value: Double // Using Double to handle both int and double values; adjust if needed
)