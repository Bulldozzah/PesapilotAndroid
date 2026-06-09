package com.example.pesapilotandroid.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary             = AppPrimary,
    onPrimary           = AppPrimaryText,
    primaryContainer    = AppSecondaryBg,
    onPrimaryContainer  = AppPrimary,
    secondary           = AppAccentBlue,
    onSecondary         = Color.White,
    secondaryContainer  = AppSecondaryBg,
    onSecondaryContainer = AppPrimary,
    tertiary            = AppGold,
    onTertiary          = Color.White,
    tertiaryContainer   = AppSecondaryBg,
    onTertiaryContainer = AppPrimary,
    background          = AppBackground,
    onBackground        = AppText,
    surface             = AppCard,
    onSurface           = AppText,
    surfaceVariant      = AppMutedBg,
    onSurfaceVariant    = AppMutedText,
    outline             = AppBorder,
    outlineVariant      = AppInputBorder,
    error               = AppRedDestructive,
    onError             = Color.White,
)

private val DarkColorScheme = darkColorScheme(
    primary             = DarkPrimary,
    onPrimary           = Color.White,
    primaryContainer    = SidebarActiveBg,
    onPrimaryContainer  = DarkText,
    secondary           = AppAccentBlue,
    onSecondary         = Color.White,
    secondaryContainer  = SidebarActiveBg,
    onSecondaryContainer = DarkText,
    tertiary            = AppGold,
    onTertiary          = Color.White,
    tertiaryContainer   = SidebarActiveBg,
    onTertiaryContainer = DarkText,
    background          = DarkBackground,
    onBackground        = DarkText,
    surface             = DarkCard,
    onSurface           = DarkText,
    surfaceVariant      = SidebarActiveBg,
    onSurfaceVariant    = DarkMutedText,
    outline             = DarkBorder,
    outlineVariant      = DarkBorder,
    error               = AppRedDestructive,
    onError             = Color.White,
)

@Composable
fun PesaPilotAndroidTheme(
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
        typography  = Typography,
        content     = content
    )
}