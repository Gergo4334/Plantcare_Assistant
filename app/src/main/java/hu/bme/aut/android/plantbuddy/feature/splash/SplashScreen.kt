package hu.bme.aut.android.plantbuddy.feature.splash

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.painterResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import hu.bme.aut.android.plantbuddy.R
import hu.bme.aut.android.plantbuddy.navigation.Screen
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    userLoggedInNavigation: () -> Unit,
    userLoggedOutNavigation: () -> Unit,
    viewModel: SplashScreenViewModel = hiltViewModel()
) {
    val rotationState = remember { Animatable(0f) }

    // Animáció
    LaunchedEffect(key1 = Unit) {
        rotationState.animateTo(
            targetValue = 360f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 2000),
                repeatMode = RepeatMode.Restart
            )
        )
    }

    // Navigáció
    LaunchedEffect(key1 = Unit) {
        delay(3000)
        if (viewModel.isUserLoggedIn()) {
            userLoggedInNavigation()
        } else {
            userLoggedOutNavigation()
        }
    }

    Box(modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center) {
        Image(
            modifier = Modifier.fillMaxSize(),
            bitmap = ImageBitmap.imageResource(id = R.drawable.leaf),
            contentScale = ContentScale.FillBounds,
            contentDescription = null
        )

        Image(
            painter = painterResource(id = R.drawable.loading_logo),
            contentDescription = null,
            modifier = Modifier
                .graphicsLayer(rotationZ = rotationState.value)
        )
    }
}