package com.example.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

private val VidooDarkColorScheme = darkColorScheme(
    primary = VidooCyan,
    onPrimary = Color(0xFF001F24),
    primaryContainer = Color(0xFF004E5C),
    onPrimaryContainer = Color(0xFFB8F4FF),
    secondary = VidooPurple,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFF2D2159),
    onSecondaryContainer = Color(0xFFE5DEFF),
    tertiary = VidooCoinGold,
    onTertiary = Color(0xFF261900),
    tertiaryContainer = Color(0xFF4D3400),
    onTertiaryContainer = VidooRewardSoft,
    background = VidooBackgroundDark,
    onBackground = VidooTextPrimaryDark,
    surface = VidooSurfaceDark,
    onSurface = VidooTextPrimaryDark,
    surfaceVariant = VidooSurfaceSecondaryDark,
    onSurfaceVariant = VidooTextSecondaryDark,
    surfaceContainer = VidooSurfaceDark,
    surfaceContainerHigh = VidooSurfaceSecondaryDark,
    surfaceContainerHighest = VidooSurfaceElevatedDark,
    outline = VidooBorderDark,
    error = VidooError,
    onError = Color.White
)

private val VidooLightColorScheme = lightColorScheme(
    primary = VidooCyanDark,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFD0F8FF),
    onPrimaryContainer = Color(0xFF001F26),
    secondary = VidooPurple,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFE9E2FF),
    onSecondaryContainer = Color(0xFF221357),
    tertiary = VidooRewardOrange,
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFFFECB3),
    onTertiaryContainer = Color(0xFF2D1600),
    background = VidooBackgroundLight,
    onBackground = VidooTextPrimaryLight,
    surface = VidooSurfaceLight,
    onSurface = VidooTextPrimaryLight,
    surfaceVariant = VidooSurfaceSecondaryLight,
    onSurfaceVariant = VidooTextSecondaryLight,
    surfaceContainer = VidooSurfaceLight,
    surfaceContainerHigh = VidooSurfaceSecondaryLight,
    surfaceContainerHighest = VidooSurfaceElevatedLight,
    outline = VidooBorderLight,
    error = VidooError,
    onError = Color.White
)

val VidooShapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(22.dp),
    extraLarge = RoundedCornerShape(28.dp)
)

@Composable
fun VidooTheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) VidooDarkColorScheme else VidooLightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = VidooShapes,
        content = content
    )
}

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    VidooTheme(darkTheme = darkTheme, content = content)
}
