package com.farooq.diceroll.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.farooq.diceroll.ui.theme.AppGradients
import com.farooq.diceroll.ui.theme.ElectricBlue
import com.farooq.diceroll.ui.theme.LocalIsDarkTheme
import com.farooq.diceroll.ui.theme.Purple500

/**
 * The dark navy / deep purple gradient that sits behind every screen, with two soft glows in the
 * corners. Because it is a gradient (and not a picture), it looks perfect on any screen size and
 * costs no memory.
 */
@Composable
fun AppBackground(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit,
) {
    val isDark = LocalIsDarkTheme.current

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(AppGradients.background(isDark)),
    ) {
        Glow(
            color = Purple500,
            alpha = if (isDark) 0.30f else 0.18f,
            size = 340.dp,
            alignment = Alignment.TopEnd,
            offsetX = 90.dp,
            offsetY = (-130).dp,
        )
        Glow(
            color = ElectricBlue,
            alpha = if (isDark) 0.20f else 0.12f,
            size = 300.dp,
            alignment = Alignment.BottomStart,
            offsetX = (-110).dp,
            offsetY = 110.dp,
        )
        content()
    }
}

@Composable
private fun BoxScope.Glow(
    color: Color,
    alpha: Float,
    size: Dp,
    alignment: Alignment,
    offsetX: Dp = 0.dp,
    offsetY: Dp = 0.dp,
) {
    Box(
        modifier = Modifier
            .align(alignment)
            .offset(x = offsetX, y = offsetY)
            .size(size)
            .background(
                Brush.radialGradient(
                    colors = listOf(color.copy(alpha = alpha), Color.Transparent),
                ),
            ),
    )
}
