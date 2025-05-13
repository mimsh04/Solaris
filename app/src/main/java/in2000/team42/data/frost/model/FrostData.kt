package in2000.team42.data.frost.model

import kotlinx.serialization.Serializable

@Serializable
data class FrostData(
    val stationId: String?,
    val referenceTime: String,
    val temperature: Double?,
    val snow: Double?,
    val cloudAreaFraction: Double?,
    val qualityCode: Int? = null
)

sealed class FrostResult {
    data class Success(val data: List<FrostData>) : FrostResult()
    data class Failure(val message: String) : FrostResult()
}

// Helper class for deserialization
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