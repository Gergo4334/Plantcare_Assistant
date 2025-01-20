package hu.bme.aut.android.plantbuddy.data.service

import android.net.Uri
import com.google.android.gms.tasks.Task
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ListResult
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import hu.bme.aut.android.plantbuddy.data.service.firebase.storage.FirebaseStorageServiceImpl
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class FirebaseStorageServiceImplTest {
    private lateinit var mockStorage: FirebaseStorage
    private lateinit var mockStorageRef: StorageReference
    private lateinit var mockUploadTask: UploadTask
    private lateinit var mockDownloadUrl: Task<Uri>
    private lateinit var mockUri: Uri
    private lateinit var mockListResult: ListResult

    @Before
    fun setUp() {
        mockStorage = mockk(relaxed = true)
        mockStorageRef = mockk(relaxed = true)
        mockUploadTask = mockk(relaxed = true)
        mockDownloadUrl = mockk(relaxed = true)
        mockUri = mockk(relaxed = true)
        mockListResult = mockk(relaxed = true)
    }

    @Test
    fun `uploadImage should return success with download URL`() = runTest {
        every { mockStorage.reference.child(any()) } returns mockStorageRef
        every { mockStorageRef.putFile(any()) } returns mockUploadTask
        coEvery { mockUploadTask.await() } returns mockk<UploadTask.TaskSnapshot>()
        every { mockStorageRef.downloadUrl } returns mockDownloadUrl
        coEvery { mockDownloadUrl.await() } returns mockUri
        every { mockUri.toString() } returns "https://someurl.com/image.jpg"

        val firebaseStorageService = FirebaseStorageServiceImpl(mockStorage)

        val result = firebaseStorageService.uploadImage(mockk(), "plantId", false)

        assertTrue(result.isSuccess)
        assertEquals("https://someurl.com/image.jpg", result.getOrNull())
    }

    @Test
    fun `getImagesForPlant should return list of image URLs`() = runTest {
        every { mockStorage.reference.child(any()) } returns mockStorageRef
        every { mockStorageRef.listAll() } returns mockk<Task<ListResult>>()
        every { mockListResult.items } returns listOf(mockStorageRef)
        every { mockStorageRef.downloadUrl } returns mockDownloadUrl
        coEvery { mockDownloadUrl.await() } returns mockUri
        every { mockUri.toString() } returns "https://someurl.com/image.jpg"

        val firebaseStorageService = FirebaseStorageServiceImpl(mockStorage)

        val result = firebaseStorageService.getImagesForPlant("plantId")

        assertTrue(result.isSuccess)
        assertEquals(listOf("https://someurl.com/image.jpg"), result.getOrNull())
    }

    @Test
    fun `uploadImage should return failure when an error occurs`() = runTest {
        every { mockStorage.reference.child(any()) } returns mockStorageRef
        every { mockStorageRef.putFile(any()) } returns mockUploadTask
        coEvery { mockUploadTask.await() } throws Exception("Upload failed")

        val firebaseStorageService = FirebaseStorageServiceImpl(mockStorage)

        val result = firebaseStorageService.uploadImage(mockk(), "plantId", false)

        assertTrue(result.isFailure)
    }
}