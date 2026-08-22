package com.emely.gastosapp.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = Color(0xFF146C5A),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFB7F1DD),
    onPrimaryContainer = Color(0xFF002019),
    secondary = Color(0xFF456179),
    secondaryContainer = Color(0xFFD1E5FA),
    background = Color(0xFFF7F9F7),
    surface = Color(0xFFFFFFFF),
    surfaceVariant = Color(0xFFE2E8E4)
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFF9BD5C3),
    onPrimary = Color(0xFF00382D),
    primaryContainer = Color(0xFF005143),
    secondary = Color(0xFFB2C9E0),
    secondaryContainer = Color(0xFF2E4960),
    background = Color(0xFF101513),
    surface = Color(0xFF171D1A),
    surfaceVariant = Color(0xFF3F4945)
)

@Composable
fun GastosAppTheme(
    darkTheme: Boolean,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        typography = Typography,
        content = content
    )
}
