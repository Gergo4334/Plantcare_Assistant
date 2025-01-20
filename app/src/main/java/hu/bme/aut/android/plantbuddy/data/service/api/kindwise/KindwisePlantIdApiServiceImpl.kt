package hu.bme.aut.android.plantbuddy.data.service.api.kindwise

import hu.bme.aut.android.plantbuddy.data.remote.api.ApiError
import hu.bme.aut.android.plantbuddy.data.remote.api.ai.kindwise.ImageIdentificationRequest
import hu.bme.aut.android.plantbuddy.data.remote.api.ai.kindwise.KindwisePlantIdApi
import hu.bme.aut.android.plantbuddy.data.remote.api.ai.kindwise.PlantIdentificationResponse
import java.io.IOException
import javax.inject.Inject

class KindwisePlantIdApiServiceImpl @Inject constructor(
    private val kindwisePlantIdApi: KindwisePlantIdApi
): KindwisePlantIdApiService {
    override suspend fun identifyPlant(
        images: List<String>,
        dateTime: String,
        similarImages: Boolean
    ): Result<PlantIdentificationResponse> {
        val requestBody = ImageIdentificationRequest(
            images = images,
            datetime = dateTime,
            similarImages = similarImages
        )

        return try {
            val response = kindwisePlantIdApi.identifyPlantByImage(requestBody)
            if (response.isSuccessful) {
                response.body()?.let {
                    Result.success(it)
                }
                ?: Result.failure(ApiError.UnknownError("No response from the api"))
            } else {
                Result.failure(ApiError.HttpError(response.code(), response.errorBody()?.string() ?: "Unknown HTTP error"))
            }
        } catch (e: IOException) {
            Result.failure(ApiError.NetworkError("Network error: ${e.message}"))
        } catch (e: Exception) {
            Result.failure(ApiError.UnknownError("Unexpected error: ${e.message}"))
        }
    }
}