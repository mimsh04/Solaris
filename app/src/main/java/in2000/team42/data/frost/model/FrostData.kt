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
                var temp: Float? = null
                var precip: Float? = null
                var cloud: Double? = null
                var quality: Int? = null
                item.observations.forEach { obs ->
                    when (obs.elementId) {
                        "air_temperature" -> temp = obs.value
                        "sum(precipitation_amount P1D)" -> precip = obs.value
                        "cloud_area_fraction" -> cloud = obs.value?.toDouble()
                    }
                    quality = obs.qualityCode ?: quality // Use the last non-null quality code
                }
                FrostData(
                    stationId = item.sourceId,
                    referenceTime = item.referenceTime,
                    temperature = temp,
                    precipitation = precip,
                    cloudAreaFraction = cloud,
                    qualityCode = quality
                )
            }
        }
    }
}




// Hjelper klasser for deserialization
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