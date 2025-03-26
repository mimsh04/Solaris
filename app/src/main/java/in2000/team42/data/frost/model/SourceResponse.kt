package in2000.team42.data.frost.model

import kotlinx.serialization.Serializable

@Serializable
data class SourceResponse(
    val context: String? = null,
    val type: String,
    val apiVersion: String,
    val license: String,
    val createdAt: String,
    val queryTime: Double,
    val currentItemCount: Int,
    val itemsPerPage: Int,
    val offset: Int,
    val totalItemCount: Int,
    val currentLink: String,
    val data: List<SensorSystem>
)

@Serializable
data class SensorSystem(
    val type: String,
    val id: String,
    val name: String,
    val shortName: String,
    val country: String,
    val countryCode: String,
    val wmoId: Int,
    val geometry: Geometry,
    val distance: Double,
    val masl: Int,
    val validFrom: String,
    val county: String,
    val countyId: Int,
    val municipality: String,
    val municipalityId: Int,
    val ontologyId: Int,
    val stationHolders: List<String>,
    val externalIds: List<String>,
    val wigosId: String
)

@Serializable
data class Geometry(
    val type: String,
    val coordinates: List<Double>,
    val nearest: Boolean
)