package in2000.team42.data.frost

import in2000.team42.model.frost.FrostResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface FrostApiService {
    @GET("observations/v0.jsonld")
    suspend fun getFrostData(
        @Query("sources") sources: String,
        @Query("elements") elements: String,
        @Query("referencetime") referenceTime: String
    ): FrostResponse
}