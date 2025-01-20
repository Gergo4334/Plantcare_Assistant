package hu.bme.aut.android.plantbuddy.data.remote.api.plant

import hu.bme.aut.android.plantbuddy.BuildConfig
import hu.bme.aut.android.plantbuddy.data.model.dto.PlantResponse
import hu.bme.aut.android.plantbuddy.data.model.dto.PlantWithDetailsDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

const val ApiAccessKey = BuildConfig.PERENUAL_API_KEY
interface PerenualApi {
    @GET("species-list")
    suspend fun getAllPlants(
        @Query("page") page: Int,
        @Query("per-page") perPager: Int,
        @Query("key") apiKey: String = ApiAccessKey
    ): Response<PlantResponse>

    @GET("species/details/{id}")
    suspend fun getPlantDetailById(
        @Path("id") id: Int,
        @Query("key") apiKey: String = ApiAccessKey
    ): Response<PlantWithDetailsDto>
}