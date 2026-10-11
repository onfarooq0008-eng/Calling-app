package com.farooq.diceroll.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.farooq.diceroll.domain.model.DiceFaces
import com.farooq.diceroll.ui.theme.AppGradients
import com.farooq.diceroll.ui.theme.DiceInk
import com.farooq.diceroll.ui.theme.DiceInkLight
import com.farooq.diceroll.ui.theme.ElectricBlue
import com.farooq.diceroll.ui.theme.LocalIsDarkTheme
import com.farooq.diceroll.ui.theme.OutlineLight
import com.farooq.diceroll.ui.theme.Purple500

/**
 * One dice, drawn completely with Compose (no image files, so it stays sharp on every screen size).
 *
 * The drawing has four parts:
 *  1. an extruded side that makes the dice look 3D,
 *  2. the white face with a soft top light,
 *  3. a thin edge so a white dice is still visible on a light background, and
 *  4. the black pips, placed with the classic dice layout from [DiceFaces].
 *
 * Passing a value outside 1..6 is impossible for a roll; if it ever happened, the dice would
 * simply show a 1 instead of crashing the app.
 */
@Composable
fun DiceView(
    face: Int,
    modifier: Modifier = Modifier,
    elevation: Dp = 16.dp,
) {
    val isDark = LocalIsDarkTheme.current
    val shape = RoundedCornerShape(percent = 18)
    val safeFace = if (DiceFaces.isValid(face)) face else DiceFaces.MIN

    Box(
        modifier = modifier
            .shadow(
                elevation = elevation,
                shape = shape,
                clip = false,
                ambientColor = Purple500.copy(alpha = 0.6f),
                spotColor = ElectricBlue.copy(alpha = 0.5f),
            )
            .clip(shape),
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawDiceFace(face = safeFace, isDark = isDark)
        }
    }
}

private fun DrawScope.drawDiceFace(face: Int, isDark: Boolean) {
    val depth = size.minDimension * 0.05f
    val faceWidth = size.width - depth
    val faceHeight = size.height - depth
    val corner = CornerRadius(size.minDimension * 0.18f)

    // 1. The 3D side, peeking out at the bottom right.
    drawRoundRect(
        brush = AppGradients.diceSide(),
        topLeft = Offset(depth, depth),
        size = Size(faceWidth, faceHeight),
        cornerRadius = corner,
    )

    // 2. The white face.
    drawRoundRect(
        brush = AppGradients.diceFace(isDark),
        topLeft = Offset.Zero,
        size = Size(faceWidth, faceHeight),
        cornerRadius = corner,
    )

    // 2b. A gentle light from above, fading out halfway down the face.
    drawRoundRect(
        brush = Brush.verticalGradient(
            colors = listOf(Color.White.copy(alpha = if (isDark) 0.65f else 0.9f), Color.Transparent),
            startY = 0f,
            endY = faceHeight * 0.55f,
        ),
        topLeft = Offset.Zero,
        size = Size(faceWidth, faceHeight * 0.55f),
        cornerRadius = corner,
    )

    // 3. A hairline edge around the face.
    drawRoundRect(
        color = if (isDark) Color.White.copy(alpha = 0.3f) else OutlineLight.copy(alpha = 0.55f),
        topLeft = Offset.Zero,
        size = Size(faceWidth, faceHeight),
        cornerRadius = corner,
        style = Stroke(width = size.minDimension * 0.006f),
    )

    // 4. The pips.
    val pipRadius = faceWidth * 0.085f
    DiceFaces.pipsFor(face).forEach { pip ->
        val center = Offset(pip.x * faceWidth, pip.y * faceHeight)
        // a soft ring that makes the pip look pressed into the dice
        drawCircle(
            color = DiceSideColor.copy(alpha = 0.35f),
            radius = pipRadius * 1.3f,
            center = center,
        )
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(DiceInkLight, DiceInk),
                center = Offset(center.x - pipRadius * 0.3f, center.y - pipRadius * 0.3f),
                radius = pipRadius * 1.8f,
            ),
            radius = pipRadius,
            center = center,
        )
    }
}

private val DiceSideColor = Color(0xFF9AA6C4)
