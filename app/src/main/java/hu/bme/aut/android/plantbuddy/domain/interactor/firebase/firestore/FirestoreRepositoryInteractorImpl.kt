package hu.bme.aut.android.plantbuddy.domain.interactor.firebase.firestore

import hu.bme.aut.android.plantbuddy.data.repositories.firebase.FirestoreRepositoryImpl
import hu.bme.aut.android.plantbuddy.domain.model.plant.user.UserPlant
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class FirestoreRepositoryInteractorImpl @Inject constructor(
    private val repository: FirestoreRepositoryImpl
): FirestoreRepositoryInteractor {
    override fun getPlants(): Flow<List<UserPlant>> = repository.plants

    override suspend fun getPlantById(id: String): UserPlant? {
        return repository.getPlantById(id)
    }

    override suspend fun savePlant(plant: UserPlant): String? {
        return repository.savePlant(plant)
    }

    override suspend fun updatePlant(plant: UserPlant) {
        repository.updatePlant(plant)
    }

    override suspend fun deletePlant(id: String) {
        repository.deletePlant(id)
    }

    override suspend fun saveFcmToken(token: String) {
        repository.saveFcmToken(token)
    }

    override suspend fun saveFavouritePlant(plantName: String) {
        repository.saveFavouritePlant(plantName)
    }

    override suspend fun getFavouritePlant(): String? {
        return repository.getFavouritePlant()
    }
}