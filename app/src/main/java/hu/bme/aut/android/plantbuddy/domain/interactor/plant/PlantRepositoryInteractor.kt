package hu.bme.aut.android.plantbuddy.domain.interactor.plant

import hu.bme.aut.android.plantbuddy.domain.model.plant.api.ApiPlant
import hu.bme.aut.android.plantbuddy.domain.model.plant.api.ApiPlantDetails

interface PlantRepositoryInteractor {
    suspend fun getAllPlants(page: Int, perPage: Int): List<ApiPlant>
    suspend fun getPlantDetailsById(id: Int): ApiPlant?
}