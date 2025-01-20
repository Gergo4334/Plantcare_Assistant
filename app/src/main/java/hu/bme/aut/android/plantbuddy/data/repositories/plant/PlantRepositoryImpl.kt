package hu.bme.aut.android.plantbuddy.data.repositories.plant

import android.util.Log
import hu.bme.aut.android.plantbuddy.data.model.dto.ApiPlantDetailsDto
import hu.bme.aut.android.plantbuddy.data.model.dto.ApiPlantDto
import hu.bme.aut.android.plantbuddy.data.remote.api.ApiError
import hu.bme.aut.android.plantbuddy.data.remote.api.plant.PerenualApi
import java.io.IOException
import javax.inject.Inject

class PlantRepositoryImpl @Inject constructor(
    private val api: PerenualApi
): PlantRepository {
    override suspend fun getAllPlants(page: Int, perPage: Int): Result<List<ApiPlantDto>> {
        return try {
            val response = api.getAllPlants(page, perPage)
            if (response.isSuccessful) {
                Result.success(response.body()?.data ?: emptyList())
            } else {
                Result.failure(ApiError.HttpError(response.code(), response.message()))
            }
        } catch (e: IOException) {
            Result.failure(ApiError.NetworkError(e.message ?: "Network connection error"))
        } catch (e: Exception) {
            Result.failure(ApiError.UnknownError(e.message ?: "An unknown error occurred"))
        }
    }

    override suspend fun getPlantDetailsById(id: Int): Result<Pair<ApiPlantDto, ApiPlantDetailsDto>> {
        return try {
            val response = api.getPlantDetailById(id)
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    val plantDto = ApiPlantDto(
                        id = body.id,
                        commonName = body.commonName,
                        cycle = body.cycle,
                        watering = body.watering,
                        sunlight = body.sunlight,
                        defaultImage = body.defaultImage
                    )
                    val plantDetailsDto = ApiPlantDetailsDto(
                        type = body.type,
                        dimensions = body.dimensions,
                        wateringGeneralBenchmark = body.wateringGeneralBenchmark,
                        indoor = body.indoor,
                        careLevel = body.careLevel,
                        flowers = body.flowers,
                        floweringSeason = body.floweringSeason,
                        growthRate = body.growthRate
                    )
                    Result.success(Pair(plantDto, plantDetailsDto))
                } else {
                    Result.failure(ApiError.UnknownError("Response body is null"))
                }
            } else {
                Result.failure(ApiError.HttpError(response.code(), response.message()))
            }
        } catch (e: IOException) {
            Result.failure(ApiError.NetworkError(e.message ?: "Network connection error"))
        } catch (e: Exception) {
            Result.failure(ApiError.UnknownError(e.message ?: "An unknown error occurred"))
        }
    }
}