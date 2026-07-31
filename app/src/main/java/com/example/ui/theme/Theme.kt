package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val LightColorScheme = lightColorScheme(
    primary = FixItBluePrimary,
    onPrimary = Color.White,
    primaryContainer = FixItBlueContainer,
    onPrimaryContainer = FixItOnBlueContainer,
    secondary = FixItBlueSecondary,
    onSecondary = Color.White,
    secondaryContainer = FixItBlueContainer,
    onSecondaryContainer = FixItOnBlueContainer,
    background = FixItBackground,
    onBackground = FixItTextNavy,
    surface = FixItSurface,
    onSurface = FixItTextPrimary,
    surfaceVariant = FixItSurfaceVariant,
    onSurfaceVariant = FixItTextSecondary,
    error = FixItError,
    errorContainer = FixItErrorContainer,
    onErrorContainer = FixItOnErrorContainer
)

private val DarkColorScheme = darkColorScheme(
    primary = FixItDarkPrimary,
    onPrimary = Color(0xFF00227B),
    primaryContainer = Color(0xFF0036A8),
    onPrimaryContainer = Color(0xFFDCE2FF),
    secondary = FixItDarkPrimary,
    onSecondary = Color(0xFF00227B),
    background = FixItDarkBackground,
    onBackground = Color(0xFFF1F5F9),
    surface = FixItDarkSurface,
    onSurface = Color(0xFFF1F5F9),
    surfaceVariant = FixItDarkSurfaceVariant,
    onSurfaceVariant = Color(0xFFCBD5E1),
    error = Color(0xFFF87171),
    errorContainer = Color(0xFF7F1D1D),
    onErrorContainer = Color(0xFFFECACA)
)

@Composable
fun FixITTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Set false to preserve brand blue theme accurately
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) = FixITTheme(darkTheme = darkTheme, dynamicColor = dynamicColor, content = content)
