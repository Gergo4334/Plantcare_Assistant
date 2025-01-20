package hu.bme.aut.android.plantbuddy.domain.interactor.plant

import android.util.Log
import hu.bme.aut.android.plantbuddy.data.repositories.plant.PlantRepositoryImpl
import hu.bme.aut.android.plantbuddy.domain.model.mapper.PlantMapper
import hu.bme.aut.android.plantbuddy.domain.model.plant.api.ApiPlant
import hu.bme.aut.android.plantbuddy.domain.model.plant.api.ApiPlantDetails
import javax.inject.Inject

class PlantRepositoryInteractorImpl @Inject constructor(
    private val repository: PlantRepositoryImpl
): PlantRepositoryInteractor {
    override suspend fun getAllPlants(page: Int, perPage: Int): List<ApiPlant> {
        val result = repository.getAllPlants(page, perPage)
        Log.d("PlantRepositoryInteractor", "API response: ${result.getOrNull()}")

        return if (result.isSuccess) {
            result.getOrNull()?.map { plantDto ->  PlantMapper.mapToApiPlant(plantDto) } ?: emptyList()
        } else {
            Log.e("PlantRepositoryInteractor", "Failed to fetch plants")
            return emptyList()
        }
    }

    override suspend fun getPlantDetailsById(id: Int): ApiPlant? {
        val result = repository.getPlantDetailsById(id)

        return if (result.isSuccess) {
            result.getOrNull()?.let { (plantDto, detailsDto) ->
                PlantMapper.mapToApiPlant(plantDto, detailsDto)
            }
        } else {
            return null
        }
    }
}