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
    tertiary = FixItLimeAccent,
    onTertiary = Color(0xFF173800),
    background = FixItBackground,
    onBackground = FixItTextNavy,
    surface = FixItSurface,
    onSurface = FixItTextPrimary,
    surfaceVariant = FixItSurfaceVariant,
    onSurfaceVariant = FixItTextSecondary,
    outline = Color(0xFF74777F),
    outlineVariant = Color(0xFFC3C7CF),
    error = FixItError,
    errorContainer = FixItErrorContainer,
    onErrorContainer = FixItOnErrorContainer
)

private val DarkColorScheme = darkColorScheme(
    primary = FixItDarkPrimary,
    onPrimary = FixItDarkOnPrimary,
    primaryContainer = FixItDarkPrimaryContainer,
    onPrimaryContainer = FixItDarkOnPrimaryContainer,
    secondary = FixItDarkSecondary,
    onSecondary = FixItDarkOnSecondary,
    tertiary = FixItDarkTertiary,
    onTertiary = FixItDarkOnTertiary,
    background = FixItDarkBackground,
    onBackground = FixItDarkOnSurface,
    surface = FixItDarkSurface,
    onSurface = FixItDarkOnSurface,
    surfaceVariant = FixItDarkSurfaceVariant,
    onSurfaceVariant = FixItDarkOnSurfaceVariant,
    outline = FixItDarkOutline,
    outlineVariant = FixItDarkOutlineVariant,
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005),
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6)
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
