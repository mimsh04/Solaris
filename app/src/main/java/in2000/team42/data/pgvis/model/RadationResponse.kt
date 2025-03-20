package in2000.team42.data.pgvis.model
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RadiationResponse(
    val inputs: Inputs? = null,
    val outputs: Outputs? = null,
    val meta: Meta? = null
)

@Serializable
data class Inputs(
    val location: Location? = null,
    val meteo_data: MeteoData? = null,
    val plane: Plane? = null,
    val time_format: String? = null
)

@Serializable
data class Location(
    val latitude: Float? = null,
    val longitude: Float? = null,
    val elevation: Int? = null
)

@Serializable
data class MeteoData(
    val radiation_db: String? = null,
    val meteo_db: String? = null,
    val year_min: Int? = null,
    val year_max: Int? = null,
    val use_horizon: Boolean? = null,
    val horizon_db: String? = null
)

@Serializable
data class Plane(
    val fixed: Fixed? = null
)

@Serializable
data class Fixed(
    val slope: SlopeAzimuth? = null,
    val azimuth: SlopeAzimuth? = null
)

@Serializable
data class SlopeAzimuth(
    val value: Int? = null,
    val optimal: Boolean? = null
)

@Serializable
data class Outputs(
    val daily_profile: List<DailyProfile>? = null
)

@Serializable
data class DailyProfile(
    val month: Int? = null,
    val time: String? = null,
    @SerialName("G(i)") val globalIrradiance: Float? = null,
    @SerialName("Gb(i)") val directIrradiance: Float? = null,
    @SerialName("Gd(i)") val diffuseIrradiance: Float? = null
)
@Serializable
data class Meta(
    val inputs: MetaInputs? = null,
    val outputs: MetaOutputs? = null
)

@Serializable
data class MetaInputs(
    val location: MetaLocation? = null,
    val meteo_data: MetaMeteoData? = null,
    val plane: MetaPlane? = null,
    val time_format: List<MetaTimeFormat>? = null
)

@Serializable
data class MetaLocation(
    val description: String? = null,
    val variables: MetaLocationVariables? = null
)

@Serializable
data class MetaLocationVariables(
    val latitude: MetaVariable? = null,
    val longitude: MetaVariable? = null,
    val elevation: MetaVariable? = null
)

@Serializable
data class MetaVariable(
    val description: String? = null,
    val units: String? = null
)

@Serializable
data class MetaMeteoData(
    val description: String? = null,
    val variables: MetaMeteoDataVariables? = null
)

@Serializable
data class MetaMeteoDataVariables(
    val radiation_db: MetaSimpleVariable? = null,
    val meteo_db: MetaSimpleVariable? = null,
    val year_min: MetaSimpleVariable? = null,
    val year_max: MetaSimpleVariable? = null,
    val use_horizon: MetaSimpleVariable? = null,
    val horizon_db: MetaSimpleVariable? = null
)

@Serializable
data class MetaSimpleVariable(
    val description: String? = null
)

@Serializable
data class MetaPlane(
    val description: String? = null,
    val fields: MetaPlaneFields? = null
)

@Serializable
data class MetaPlaneFields(
    val slope: MetaVariable? = null,
    val azimuth: MetaVariable? = null
)

@Serializable
data class MetaTimeFormat(
    val description: String? = null
)

@Serializable
data class MetaOutputs(
    val daily_profile: MetaDailyProfile? = null
)

@Serializable
data class MetaDailyProfile(
    val type: String? = null,
    val timestamp: String? = null,
    val variables: MetaDailyProfileVariables? = null
)

@Serializable
data class MetaDailyProfileVariables(
    @SerialName("G(i)") val globalIrradiance: MetaVariable? = null,
    @SerialName("Gb(i)") val directIrradiance: MetaVariable? = null,
    @SerialName("Gd(i)") val diffuseIrradiance: MetaVariable? = null
)