package com.example.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = DarkBotanical,
    onPrimary = PureWhite,
    primaryContainer = CardMint,
    onPrimaryContainer = DarkBotanical,
    secondary = ForestGreen,
    onSecondary = PureWhite,
    secondaryContainer = SoftGray,
    onSecondaryContainer = TextDark,
    tertiary = SageGreen,
    background = OffWhite,
    onBackground = TextDark,
    surface = PureWhite,
    onSurface = TextDark,
    surfaceVariant = SoftGray,
    onSurfaceVariant = TextMuted,
    outline = BorderGray
)

private val DarkColorScheme = darkColorScheme(
    primary = MintGreen,
    onPrimary = DarkBotanical,
    primaryContainer = ForestGreen,
    onPrimaryContainer = PureWhite,
    secondary = SageGreen,
    onSecondary = PureWhite,
    background = DarkBotanical,
    onBackground = PureWhite,
    surface = DarkCardSurface,
    onSurface = PureWhite,
    surfaceVariant = DarkBotanical,
    onSurfaceVariant = TextMuted,
    outline = ForestGreen
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
