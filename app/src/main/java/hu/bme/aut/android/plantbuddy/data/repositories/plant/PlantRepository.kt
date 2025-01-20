package hu.bme.aut.android.plantbuddy.data.repositories.plant

import hu.bme.aut.android.plantbuddy.data.model.dto.ApiPlantDetailsDto
import hu.bme.aut.android.plantbuddy.data.model.dto.ApiPlantDto

interface PlantRepository {
    suspend fun getAllPlants(page: Int, perPage: Int): Result<List<ApiPlantDto>>
    suspend fun getPlantDetailsById(id: Int): Result<Pair<ApiPlantDto, ApiPlantDetailsDto>>
}