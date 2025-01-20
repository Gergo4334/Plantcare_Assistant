package hu.bme.aut.android.plantbuddy.data.service.api.kindwise

import hu.bme.aut.android.plantbuddy.data.remote.api.ai.kindwise.PlantIdentificationResponse

interface KindwisePlantIdApiService {
    suspend fun identifyPlant(images: List<String>, dateTime: String, similarImages: Boolean): Result<PlantIdentificationResponse>
}