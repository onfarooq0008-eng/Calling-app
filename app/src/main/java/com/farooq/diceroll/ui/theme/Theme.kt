package com.farooq.diceroll.ui.theme

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import com.farooq.diceroll.domain.model.ThemeMode
import androidx.compose.foundation.shape.RoundedCornerShape

/**
 * `true` while the app is showing its dark theme.
 * Components use it to pick gradients (Compose has no gradient support in [MaterialTheme]).
 */
val LocalIsDarkTheme = staticCompositionLocalOf { true }

private val DarkColors = darkColorScheme(
    primary = Orange500,
    onPrimary = Color(0xFF2A1300),
    primaryContainer = Color(0xFF4A2408),
    onPrimaryContainer = Orange400,
    secondary = Blue300,
    onSecondary = Color(0xFF06121F),
    secondaryContainer = Navy600,
    onSecondaryContainer = Color(0xFFD6E7FF),
    tertiary = Purple300,
    onTertiary = Color(0xFF1B1038),
    tertiaryContainer = Purple700,
    onTertiaryContainer = Color(0xFFE7DEFF),
    background = Navy900,
    onBackground = Ice,
    surface = Navy800,
    onSurface = Ice,
    surfaceVariant = Navy700,
    onSurfaceVariant = Lavender,
    outline = OutlineDark,
    outlineVariant = Color(0xFF2A3363),
    error = Color(0xFFFF8A8A),
    onError = Color(0xFF3A0A0A),
    errorContainer = Color(0xFF5C1420),
    onErrorContainer = Color(0xFFFFD9D9),
    scrim = Color(0xE605070F),
    inverseSurface = Ice,
    inverseOnSurface = Navy900,
)

private val LightColors = lightColorScheme(
    primary = Orange600,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFFE3CE),
    onPrimaryContainer = Orange700,
    secondary = Color(0xFF1565D8),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFDCE8FF),
    onSecondaryContainer = Color(0xFF0A2E63),
    tertiary = Color(0xFF6D28D9),
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFEADDFF),
    onTertiaryContainer = Color(0xFF2A1060),
    background = LightBackgroundTop,
    onBackground = LightText,
    surface = LightSurface,
    onSurface = LightText,
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = LightTextMuted,
    outline = OutlineLight,
    outlineVariant = Color(0xFFDDE3F2),
    error = Color(0xFFB3261E),
    onError = Color.White,
    errorContainer = Color(0xFFF9DEDC),
    onErrorContainer = Color(0xFF410E0B),
    scrim = Color(0x99000000),
    inverseSurface = LightText,
    inverseOnSurface = LightSurface,
)

/** Rounded corners are part of the app's friendly, modern look. */
val AppShapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(14.dp),
    medium = RoundedCornerShape(20.dp),
    large = RoundedCornerShape(28.dp),
    extraLarge = RoundedCornerShape(34.dp),
)

/**
 * Applies the chosen theme (light, dark or the phone's setting), the colours, the typography and
 * the shapes to everything inside [content].
 *
 * @param themeMode what the user picked in the Settings screen.
 */
@Composable
fun DiceRollTheme(
    themeMode: ThemeMode = ThemeMode.DEFAULT,
    content: @Composable () -> Unit,
) {
    val darkTheme = when (themeMode) {
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
    }
    val colorScheme = if (darkTheme) DarkColors else LightColors

    // Status bar / navigation bar icons must be dark on a light background and the other way
    // around, otherwise they would be invisible.
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = view.context.findActivity()?.window ?: return@SideEffect
            val controller = WindowCompat.getInsetsController(window, view)
            controller.isAppearanceLightStatusBars = !darkTheme
            controller.isAppearanceLightNavigationBars = !darkTheme
        }
    }

    CompositionLocalProvider(LocalIsDarkTheme provides darkTheme) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = AppTypography,
            shapes = AppShapes,
            content = content,
        )
    }
}

/** Finds the Activity behind a Context, so the theme can adjust the system bars. */
private tailrec fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}
