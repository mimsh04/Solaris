package in2000.team42.data.frost.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class FrostData(
    val stationId: String?, // Optional or could be removed
    val referenceTime: String,
    val temperature: Double?,
    val precipitation: Double?,
    val cloudAreaFraction: Double?,
    val qualityCode: Int? = null
) {
    class Builder {
        var stationId: String? = null
        private var referenceTime: String? = null
        private var temperature: Double? = null
        private var precipitation: Double? = null
        private var cloudAreaFraction: Double? = null

        fun setStationId(id: String) = apply { this.stationId = id }
        fun setReferenceTime(time: String) = apply { this.referenceTime = time }
        fun setTemperature(temp: Double) = apply { this.temperature = temp }
        fun setPrecipitation(precip: Double) = apply { this.precipitation = precip }
        fun setCloudAreaFraction(cloud: Double) = apply { this.cloudAreaFraction = cloud }

        fun build() = FrostData(
            stationId,
            referenceTime ?: throw IllegalStateException("Reference time required"),
            temperature,
            precipitation,
            cloudAreaFraction
        )
    }
}

sealed class FrostResult {
    data class Success(val data: List<FrostData>) : FrostResult()
    data class Failure(val message: String) : FrostResult(), List<FrostData>
}

// Hjelper klasser for deserialization
@Serializable
data class FrostResponse(
    val data: List<FrostObservation>
)

@Serializable
data class FrostObservation(
    val sourceId: String,
    val referenceTime: String,
    val observations: List<Observation>
)

@Serializable
data class Observation(
    val elementId: String? = null,
    val value: Float? = null,
    val qualityCode: Int? = null
)

@Serializable
data class FrostErrorResponse(
    val error: ErrorDetails
) {
    @Serializable
    data class ErrorDetails(
        val code: Int,
        val message: String,
        val reason: String
    )
}