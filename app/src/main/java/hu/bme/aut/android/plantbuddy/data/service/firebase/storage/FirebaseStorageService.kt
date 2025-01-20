package hu.bme.aut.android.plantbuddy.data.service.firebase.storage

import android.net.Uri

interface FirebaseStorageService {
    suspend fun uploadImage(imageUri: Uri, plantId: String? = null, forRecognition: Boolean = false): Result<String>
    suspend fun getImagesForPlant(plantId: String): Result<List<String>>

}