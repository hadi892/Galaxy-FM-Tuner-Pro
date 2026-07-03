package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = CyanElectric,
    onPrimary = Color.Black,
    primaryContainer = CyanDeep,
    onPrimaryContainer = CyanElectric,
    secondary = AmberNeon,
    onSecondary = Color.Black,
    secondaryContainer = AmberDark,
    onSecondaryContainer = AmberNeon,
    tertiary = MagentaLaser,
    background = GalacticBackground,
    onBackground = OffWhite,
    surface = CarbonSurface,
    onSurface = OffWhite,
    surfaceVariant = SlateGlass,
    onSurfaceVariant = MutedSlate
)

private val LightColorScheme = darkColorScheme(
    // We strictly use the dark futuristic audio tuner aesthetic by default
    primary = CyanElectric,
    onPrimary = Color.Black,
    primaryContainer = CyanDeep,
    onPrimaryContainer = CyanElectric,
    secondary = AmberNeon,
    onSecondary = Color.Black,
    secondaryContainer = AmberDark,
    onSecondaryContainer = AmberNeon,
    tertiary = MagentaLaser,
    background = GalacticBackground,
    onBackground = OffWhite,
    surface = CarbonSurface,
    onSurface = OffWhite,
    surfaceVariant = SlateGlass,
    onSurfaceVariant = MutedSlate
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Disable dynamic color to maintain sleek dark tuner look
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}
