package hu.bme.aut.android.plantbuddy.data.repositories.firebase

import hu.bme.aut.android.plantbuddy.domain.model.plant.user.UserPlant

interface FirestoreRepository {
    suspend fun getPlantById(firestoreId: String): UserPlant?
    suspend fun savePlant(plant: UserPlant): String?
    suspend fun updatePlant(plant: UserPlant)
    suspend fun deletePlant(id: String)
    suspend fun saveFcmToken(token: String)
    suspend fun saveFavouritePlant(plantName: String)
    suspend fun getFavouritePlant(): String?
}