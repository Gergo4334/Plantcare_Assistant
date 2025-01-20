package hu.bme.aut.android.plantbuddy.domain.interactor.firebase.storage

import android.net.Uri

interface FirebaseStorageServiceInteractor {
    suspend fun uploadImage(imageUri: Uri, plantId: String? = null, forRecognition: Boolean = false): Result<String>
    suspend fun getImagesForPlant(plantId: String): Result<List<String>>
}