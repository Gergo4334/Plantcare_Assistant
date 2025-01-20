package hu.bme.aut.android.plantbuddy.data.remote.api.ai.kindwise

import com.squareup.moshi.Json
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
interface KindwisePlantIdApi {
    @POST("identification")
    suspend fun identifyPlantByImage(
        @Body requestBody: ImageIdentificationRequest
    ): Response<PlantIdentificationResponse>
}

data class ImageIdentificationRequest(
    val images: List<String>,
    val datetime: String? = null,
    @field:Json(name = "similar_images") val similarImages: Boolean
)

data class PlantIdentificationResponse(
    val result: PlantResult
)

data class PlantResult(
    val classification: Classification
)

data class Classification(
    val suggestions: List<Suggestion>
)

data class Suggestion(
    val name: String,
    val probability: Float,
    @field:Json(name = "similar_images") val similarImages: List<SimilarImage>?
)

data class SimilarImage(
    val url: String
)
