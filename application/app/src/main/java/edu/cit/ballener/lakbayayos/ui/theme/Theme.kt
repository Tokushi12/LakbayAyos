package edu.cit.ballener.lakbayayos.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LakbayAyosColorScheme = darkColorScheme(
    primary = RacingRed,
    onPrimary = TextPrimary,
    background = BackgroundBlack,
    onBackground = TextPrimary,
    surface = SurfaceDark,
    onSurface = TextPrimary,
    secondary = TextSecondary,
    error = ErrorRed,
    onError = Color.White,
    outline = BorderDark
)

@Composable
fun LakbayAyosTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LakbayAyosColorScheme,
        typography = MaterialTheme.typography,
        content = content
    )
}
