package hu.bme.aut.android.plantbuddy.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

val Green = Color(0xFF8BC34A)
val LightGreen = Color(0xFFB8F0C3)

@Composable
fun GradientBox(
    modifier: Modifier = Modifier,
    colors: List<Color> = listOf(LightGreen, Green),
    content: @Composable BoxScope.() -> Unit
) {
    Box(
       modifier = modifier.background(brush = Brush.linearGradient(
           colors
       ))
    ) {
        content()
    }
}