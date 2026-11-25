package com.example.tanico.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = DarkForestGreen,
    onPrimary = TextPrimary,
    primaryContainer = ForestGreen,
    onPrimaryContainer = SoftWhite,
    secondary = DarkEarthBrown,
    onSecondary = TextPrimary,
    tertiary = DarkFreshGreen,
    onTertiary = TextPrimary,
    background = DarkWarmCream,
    onBackground = DarkTextPrimary,
    surface = DarkSurface,
    onSurface = DarkTextPrimary,
    surfaceVariant = DarkSurface,
    onSurfaceVariant = DarkTextSecondary,
    outline = DarkNaturalBeige,
    outlineVariant = DarkNaturalBeige,
    secondaryContainer = Color(0xFF394238),
    onSecondaryContainer = DarkTextPrimary,
    surfaceTint = DarkForestGreen,
    error = TanicoError,
    onError = SoftWhite
)

private val LightColorScheme = lightColorScheme(
    primary = ForestGreen,
    onPrimary = SoftWhite,
    primaryContainer = Color(0xFFDDE9D9),
    onPrimaryContainer = TextPrimary,
    secondary = EarthBrown,
    onSecondary = SoftWhite,
    tertiary = FreshGreen,
    onTertiary = SoftWhite,
    background = WarmCream,
    onBackground = TextPrimary,
    surface = SoftWhite,
    onSurface = TextPrimary,
    surfaceVariant = WarmCream,
    onSurfaceVariant = TextSecondary,
    outline = NaturalBeige,
    outlineVariant = NaturalBeige,
    secondaryContainer = Color(0xFFE6ECE1),
    onSecondaryContainer = ForestGreen,
    surfaceTint = ForestGreen,
    surfaceContainer = Color(0xFFF0EEE8),
    surfaceContainerLow = WarmCream,
    surfaceContainerHigh = Color(0xFFE9E6DE),
    surfaceContainerHighest = Color(0xFFE4E1D9),
    error = TanicoError,
    onError = SoftWhite
)

@Composable
fun TanicoTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme,
        typography = Typography,
        shapes = TanicoShapes,
        content = content
    )
}