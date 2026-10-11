package com.farooq.diceroll.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.farooq.diceroll.domain.roll.RollTiming
import kotlinx.coroutines.launch

/**
 * A dice that floats gently while waiting and tumbles while rolling.
 *
 * While [isRolling] is true the dice
 *  - spins (accelerating and slowing down again, like a real throw),
 *  - hops three times, and
 *  - changes its face, because the ViewModel hands in a new value every few milliseconds.
 *
 * When the roll finishes, the dice gives a small "landing" bounce. Everything stops by itself:
 * there is no animation running while the app is idle (apart from the gentle floating), which
 * keeps the app easy on the battery.
 *
 * @param index the position of this dice (0 or 1) - it shifts the floating phase of the second
 * dice so both dice do not move in perfect sync.
 */
@Composable
fun AnimatedDice(
    face: Int,
    isRolling: Boolean,
    diceSize: Dp,
    index: Int = 0,
    modifier: Modifier = Modifier,
) {
    val density = LocalDensity.current
    val floatAmplitude = remember(density) { with(density) { FLOAT_AMPLITUDE.toPx() } }
    val hopHeight = remember(density) { with(density) { HOP_HEIGHT.toPx() } }

    // The dice floats up and down while it waits for the next roll.
    val floating = rememberInfiniteTransition(label = "diceFloat$index")
    val floatOffset by floating.animateFloat(
        initialValue = -floatAmplitude / 2f,
        targetValue = floatAmplitude / 2f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = FLOAT_DURATION_MS,
                easing = LinearEasing,
                delayMillis = index * FLOAT_DELAY_MS,
            ),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "floatOffset",
    )

    val spin = remember { Animatable(0f) }
    val hop = remember { Animatable(0f) }
    val settle = remember { Animatable(1f) }

    LaunchedEffect(isRolling, index) {
        if (isRolling) {
            spin.snapTo(0f)
            hop.snapTo(0f)
            // Two full turns, slow at the start and slow at the end.
            launch {
                spin.animateTo(
                    targetValue = SPIN_DEGREES,
                    animationSpec = tween(durationMillis = ROLL_DURATION_MS, easing = FastOutSlowInEasing),
                )
            }
            launch {
                repeat(HOP_COUNT) {
                    hop.animateTo(-hopHeight, animationSpec = tween(HOP_STEP_MS, easing = FastOutSlowInEasing))
                    hop.animateTo(0f, animationSpec = tween(HOP_STEP_MS, easing = LinearOutSlowInEasing))
                }
            }
        } else {
            // The dice just landed: a short squeeze into place.
            settle.snapTo(1.07f)
            settle.animateTo(
                targetValue = 1f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMedium,
                ),
            )
            spin.snapTo(0f)
            hop.snapTo(0f)
        }
    }

    Box(
        modifier = modifier
            .size(diceSize)
            .graphicsLayer {
                translationY = floatOffset + hop.value
                rotationZ = spin.value
                scaleX = settle.value
                scaleY = settle.value
            },
    ) {
        DiceView(
            face = face,
            modifier = Modifier.fillMaxSize(),
            elevation = if (isRolling) 26.dp else 18.dp,
        )
    }
}

/** How far the dice floats up and down while waiting. */
private val FLOAT_AMPLITUDE = 9.dp

/** How high the dice hops while rolling. */
private val HOP_HEIGHT = 18.dp

private const val FLOAT_DURATION_MS = 2600
private const val FLOAT_DELAY_MS = 420
private const val SPIN_DEGREES = 720f
private const val HOP_COUNT = 3
private const val HOP_STEP_MS = 150

/** 3 hops up and down (3 x 2 x 150 ms) = the length of one roll. */
private val ROLL_DURATION_MS = RollTiming.DURATION_MS.toInt()
