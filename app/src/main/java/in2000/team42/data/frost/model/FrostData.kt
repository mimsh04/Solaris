package in2000.team42.data.frost.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class FrostData(
    val stationId: String,
    val referenceTime: String,
    val temperature: Float? = null, // I Celsius
    val snow: Float? = null, // I mm
    val cloudAreaFraction: Double? = null, // I deler fra 1-8 der 8 er fulldekket
    val qualityCode: Int? = null,
) {
    companion object {
        fun fromJson(jsonString: String): List<FrostData> {
            val json = Json { ignoreUnknownKeys = true }
            val response = json.decodeFromString<FrostResponse>(jsonString)
            return response.data.map { item ->
                var temp: Float? = null
                var snow: Float? = null
                var cloud: Double? = null
                var quality: Int? = null
                item.observations.forEach { obs ->
                    when (obs.elementId) {
                        "best_estimate_mean(air_temperature P1M)" -> temp = obs.value
                        "mean(snow_coverage_type P1M)" -> snow = obs.value
                        "mean(cloud_area_fraction P1M)" -> cloud = obs.value?.toDouble()
                    }
                    quality = obs.qualityCode ?: quality
                }
                FrostData(
                    stationId = item.sourceId,
                    referenceTime = item.referenceTime,
                    temperature = temp,
                    snow = snow,
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