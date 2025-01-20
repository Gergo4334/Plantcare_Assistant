package hu.bme.aut.android.plantbuddy.domain.interactor.firebase.firestore

import hu.bme.aut.android.plantbuddy.domain.model.plant.user.UserPlant
import kotlinx.coroutines.flow.Flow

interface FirestoreRepositoryInteractor {
    fun getPlants(): Flow<List<UserPlant>>
    suspend fun getPlantById(id: String): UserPlant?
    suspend fun savePlant(plant: UserPlant): String?
    suspend fun updatePlant(plant: UserPlant)
    suspend fun deletePlant(id: String)
    suspend fun saveFcmToken(token: String)
    suspend fun saveFavouritePlant(plantName: String)
    suspend fun getFavouritePlant(): String?
}