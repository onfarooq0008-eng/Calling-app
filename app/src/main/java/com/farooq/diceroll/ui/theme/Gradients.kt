package com.farooq.diceroll.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

/**
 * All gradients of the app in one place: the dark navy background, the cards, the dice and the
 * orange ROLL DICE button.
 */
object AppGradients {

    /** The background behind every screen. */
    fun background(isDark: Boolean): Brush = if (isDark) {
        Brush.verticalGradient(listOf(Navy800, PurpleDeep, Navy900))
    } else {
        Brush.verticalGradient(listOf(LightBackgroundTop, LightBackgroundBottom))
    }

    /** Rounded cards: slightly lighter than the background so they "float". */
    fun card(isDark: Boolean): Brush = if (isDark) {
        Brush.verticalGradient(listOf(Color(0xFF1B2452), Color(0xFF141A3D)))
    } else {
        Brush.verticalGradient(listOf(LightSurface, Color(0xFFF5F7FE)))
    }

    /** The small dice logo in the top bar. */
    fun brandMark(): Brush = Brush.linearGradient(listOf(Purple500, ElectricBlue))

    /** The white face of a dice. */
    fun diceFace(isDark: Boolean): Brush = Brush.linearGradient(
        listOf(Color.White, if (isDark) Color(0xFFE1E8F8) else Color(0xFFEDF0FA)),
    )

    /** The extruded side of the dice that gives it the 3D look. */
    fun diceSide(): Brush = Brush.verticalGradient(listOf(DiceSideLight, DiceSideDark))

    /** The big ROLL DICE button. */
    fun rollButton(enabled: Boolean): Brush = if (enabled) {
        Brush.linearGradient(listOf(Orange400, Orange500, Orange600))
    } else {
        Brush.linearGradient(listOf(Color(0xFF9C7C68), Color(0xFF80634F)))
    }

    /** The moving pill behind the selected dice mode. */
    fun modeIndicator(isDark: Boolean): Brush = if (isDark) {
        Brush.linearGradient(listOf(Color(0xFF2C3873), Color(0xFF3A2C6E)))
    } else {
        Brush.linearGradient(listOf(Color.White, Color.White))
    }

    /** Small pills, e.g. the total of two dice. */
    fun chip(): Brush = Brush.linearGradient(
        listOf(ElectricBlue.copy(alpha = 0.22f), Purple300.copy(alpha = 0.18f)),
    )
}
