package in2000.team42.data.frost.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class FrostData(
    val stationId: String,
    val referenceTime: String,
    val temperature: Float? = null, // I Celsius
    val precipitation: Float? = null, // I mm
    val cloudAreaFraction: Double? = null, // I prosent
    val qualityCode: Int? = null,
) {
    companion object {
        fun fromJson(jsonString: String): List<FrostData> {
            val json = Json { ignoreUnknownKeys = true }
            val response = json.decodeFromString<FrostResponse>(jsonString)
            return response.data.map { item ->
                val observation = item.observations.firstOrNull() ?: Observation()
                FrostData(
                    stationId = item.sourceId,
                    referenceTime = item.referenceTime,
                    temperature = if (observation.elementId == "air_temperature") observation.value else null,
                    precipitation = if (observation.elementId == "sum(precipitation_amount P1D)") observation.value else null,
                    cloudAreaFraction = ((if (observation.elementId == "cloud_area_fraction") observation.value else null) as Double?),
                    qualityCode = observation.qualityCode
                )
            }
        }
    }
}

// Helper classes for deserialization
@Serializable
private data class FrostResponse(
    val data: List<FrostObservation>
)

@Serializable
private data class FrostObservation(
    val sourceId: String,
    val referenceTime: String,
    val observations: List<Observation>
)

@Serializable
private data class Observation(
    val elementId: String? = null,
    val value: Float? = null,
    val qualityCode: Int? = null
)