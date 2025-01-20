package hu.bme.aut.android.plantbuddy.data.service.firebase.storage

import android.net.Uri
import com.google.firebase.storage.FirebaseStorage
import hu.bme.aut.android.plantbuddy.data.service.firebase.storage.FirebaseStorageService
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import kotlin.math.truncate

class FirebaseStorageServiceImpl @Inject constructor(
    private val storage: FirebaseStorage
): FirebaseStorageService {
    override suspend fun uploadImage(imageUri: Uri, plantId: String?, forRecognition: Boolean): Result<String> {
        return try {
            val folder = if (forRecognition) "recognition" else "plants/${plantId}"
            val storageRef = storage.reference.child("${folder}/${System.currentTimeMillis()}.jpg")
            storageRef.putFile(imageUri).await()
            val downloadUrl = storageRef.downloadUrl.await()
            Result.success(downloadUrl.toString())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getImagesForPlant(plantId: String): Result<List<String>> {
        return try {
            val folderRef = storage.reference.child("plants/$plantId")
            val imageUrls = mutableListOf<String>()
            val result = folderRef.listAll().await()
            result.items.forEach { storageRef ->
                val downloadUrl = storageRef.downloadUrl.await()
                imageUrls.add(downloadUrl.toString())
            }
            Result.success(imageUrls)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}