package hu.bme.aut.android.plantbuddy.ui.common

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat

@Composable
fun CameraCapture(
    launchCamera: Boolean,
    onImageFile: (Uri) -> Unit
) {
    val context = LocalContext.current
    val imageUri = remember { mutableStateOf<Uri?>(null) }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            imageUri.value?.let {
                onImageFile(it)
                Log.d("CameraCapture", "Image captured: $it")
            } ?: Log.e("CameraCapture", "Image URI is null")
        } else {
            imageUri.value = null
            Log.e("CameraCapture", "Image capture failed")
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            val uri = createImageUri(context)
            if (uri != null) {
                imageUri.value = uri
                cameraLauncher.launch(uri)
            }
        } else {
            Toast.makeText(context, "Camera permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    if(launchCamera) {
        val cameraPermission = Manifest.permission.CAMERA
        when (PackageManager.PERMISSION_GRANTED) {
            ContextCompat.checkSelfPermission(context, cameraPermission) -> {
                val uri = createImageUri(context)
                if (uri != null) {
                    imageUri.value = uri
                    cameraLauncher.launch(uri)
                }
            }

            else -> {
                permissionLauncher.launch(cameraPermission)
            }
        }
    }
}

fun createImageUri(context: Context): Uri? {
    val contentValues = ContentValues().apply {
        put(MediaStore.Images.Media.DISPLAY_NAME, "plant_image_${System.currentTimeMillis()}.jpg")
        put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
        put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/PlantBuddy")
    }

    return context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
}

@Composable
@Preview
fun CameraCapture_Preview() {
    CameraCapture(
        launchCamera = false,
        onImageFile = { }
    )
}