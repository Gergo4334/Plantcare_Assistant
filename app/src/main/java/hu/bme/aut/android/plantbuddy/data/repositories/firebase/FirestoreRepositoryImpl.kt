package hu.bme.aut.android.plantbuddy.data.repositories.firebase

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.snapshots
import com.google.firebase.firestore.toObject
import com.google.firebase.firestore.toObjects
import hu.bme.aut.android.plantbuddy.data.service.auth.AuthService
import hu.bme.aut.android.plantbuddy.domain.model.plant.firebase.FirebasePlant
import hu.bme.aut.android.plantbuddy.domain.model.plant.firebase.asFirebasePlant
import hu.bme.aut.android.plantbuddy.domain.model.plant.firebase.asUserPlant
import hu.bme.aut.android.plantbuddy.domain.model.plant.user.UserPlant
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirestoreRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val authService: AuthService
): FirestoreRepository {

    @OptIn(ExperimentalCoroutinesApi::class)
    val plants: Flow<List<UserPlant>> = authService.currentUser.flatMapLatest { user ->
        if (user == null) flow { emit(emptyList()) }
        else currentCollection(user.id)
            .snapshots()
            .map { snapshot ->
                snapshot
                    .toObjects<FirebasePlant>()
                    .map {
                        it.asUserPlant()
                    }
            }
    }
    companion object {
        private const val USER_COLLECTION = "users"
        private const val PLANT_COLLECTION = "plants"
    }

    private fun currentCollection(userId: String) =
        firestore.collection(USER_COLLECTION).document(userId).collection(PLANT_COLLECTION)

    private fun currentUserDocument(userId: String) =
        firestore.collection(USER_COLLECTION).document(userId)

    override suspend fun getPlantById(firestoreId: String): UserPlant? =
        authService.currentUserId?.let {
            currentCollection(it).document(firestoreId).get().await().toObject<FirebasePlant>()?.asUserPlant()
        }

    override suspend fun savePlant(plant: UserPlant): String? {
        authService.currentUserId?.let {
           return currentCollection(it).add(plant.asFirebasePlant()).await().id
        }
        return null
    }

    override suspend fun updatePlant(plant: UserPlant) {
        authService.currentUserId?.let {
            currentCollection(it).document(plant.id).set(plant.asFirebasePlant()).await()
        }
    }

    override suspend fun deletePlant(id: String) {
        authService.currentUserId?.let {
            currentCollection(it).document(id).delete().await()
        }
    }

    override suspend fun saveFcmToken(token: String) {
        authService.currentUserId?.let {
            currentUserDocument(it)
                .set(mapOf("fcmToken" to token))
        }
    }

    override suspend fun saveFavouritePlant(plantName: String) {
        authService.currentUserId?.let {
            currentUserDocument(it)
                .set(mapOf("favourite plant" to plantName))
        }
    }

    override suspend fun getFavouritePlant(): String? =
        authService.currentUserId?.let {
            currentUserDocument(it).get().await().getString("favourite plant")
        }
}