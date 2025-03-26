package in2000.team42.data.frost.model

import kotlinx.serialization.Serializable

@Serializable
data class FrostData(
    val temperature: Double?,          // °C
    val snowWaterEquivalent: Double?,  // kg/m²
    val cloudCoverage: Int?            // %
)

// metadata
data class FrostResponse(
    val data: List<ObservationData> // List of observation entries from the API
)

// Represents a single observation set in the response
// Representerer et set med observasjoner i en repsonse
data class ObservationData(
    val observations: List<Observation> // Liste av individuelle maalinger (temp, snow, clouds)
)

data class Observation(
    val elementId: String, // Identifikator som "air_temperature" eller "cloud_area_fraction"
    val value: Double // Bruker double for å håndtere både Double og int verdier
)