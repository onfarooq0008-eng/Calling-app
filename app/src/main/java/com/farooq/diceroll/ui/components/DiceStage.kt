package com.farooq.diceroll.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.farooq.diceroll.R

/**
 * The centre of the screen: one dice, or two dice in two dice mode.
 *
 * Tapping anywhere on the dice (plus a little padding around them) starts a roll - that is the
 * "tap the dice" gesture. While the dice are rolling the tap area is disabled, so a second tap
 * cannot disturb the running animation.
 *
 * The dice are sized from the space that is actually available, so they look great on a small
 * phone as well as on a tablet, in portrait and in landscape.
 */
@Composable
fun DiceStage(
    faces: List<Int>,
    isRolling: Boolean,
    onRoll: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val pressScale by animateFloatAsState(
        targetValue = if (pressed && !isRolling) 0.96f else 1f,
        animationSpec = tween(durationMillis = 120),
        label = "dicePress",
    )
    val diceCount = faces.size.coerceAtLeast(1)

    // Screen readers announce the dice too, so the game is playable without looking at it:
    // "Dice showing 4 + 5". The tap area is a button, so TalkBack also offers "double tap to
    // roll the dice".
    val diceDescription = stringResource(R.string.cd_dice_values, faces.joinToString(" + "))

    BoxWithConstraints(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center,
    ) {
        // Two dice need more room, so they are a little smaller than a single dice.
        val divisor = if (diceCount == 1) 1.9f else 2.75f
        val diceSize = (maxWidth / divisor).coerceIn(96.dp, 232.dp)

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(32.dp))
                .semantics(mergeDescendants = true) { contentDescription = diceDescription }
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    enabled = !isRolling,
                    onClickLabel = stringResource(R.string.cd_roll_dice),
                    role = Role.Button,
                    onClick = onRoll,
                )
                .padding(18.dp)
                .graphicsLayer {
                    scaleX = pressScale
                    scaleY = pressScale
                },
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                faces.forEachIndexed { index, face ->
                    AnimatedDice(
                        face = face,
                        isRolling = isRolling,
                        diceSize = diceSize,
                        index = index,
                    )
                }
            }
        }
    }
}
