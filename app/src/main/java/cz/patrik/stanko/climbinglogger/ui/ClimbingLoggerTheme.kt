package cz.patrik.stanko.climbinglogger.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

// jednoduché světlo/tma schéma, můžeš si doladit barvy
private val lightColors = lightColorScheme()
private val darkColors = darkColorScheme()

@Composable
fun ClimbingLoggerTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) darkColors else lightColors

    MaterialTheme(
        colorScheme = colors,
        typography = MaterialTheme.typography,
        shapes = MaterialTheme.shapes,
        content = content
    )
}
