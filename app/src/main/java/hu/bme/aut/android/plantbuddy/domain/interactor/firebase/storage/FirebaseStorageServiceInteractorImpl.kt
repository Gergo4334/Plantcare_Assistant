package hu.bme.aut.android.plantbuddy.domain.interactor.firebase.storage

import android.net.Uri
import hu.bme.aut.android.plantbuddy.data.service.firebase.storage.FirebaseStorageService
import javax.inject.Inject

class FirebaseStorageServiceInteractorImpl @Inject constructor(
    private val firebaseStorageService: FirebaseStorageService
): FirebaseStorageServiceInteractor {
    override suspend fun uploadImage(imageUri: Uri, plantId: String?, forRecognition: Boolean): Result<String> {
        return firebaseStorageService.uploadImage(imageUri, plantId, forRecognition)
    }

    override suspend fun getImagesForPlant(plantId: String): Result<List<String>> {
        return  firebaseStorageService.getImagesForPlant(plantId)
    }
}