package com.farooq.diceroll.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.farooq.diceroll.R
import com.farooq.diceroll.domain.model.DiceMode
import com.farooq.diceroll.domain.model.RollRecord
import com.farooq.diceroll.ui.roll.RollState
import com.farooq.diceroll.ui.theme.AppGradients

/**
 * The big result text in the middle of the screen: "You rolled a 6!" or
 * "You rolled 4 + 5 = 9!", plus a small "Total 9" pill in two dice mode.
 *
 * The text fades in whenever it changes, which makes the result feel like part of the animation.
 */
@Composable
fun ResultBanner(
    state: RollState,
    mode: DiceMode,
    modifier: Modifier = Modifier,
) {
    val record = state.lastRecord
    val text = when {
        state.isRolling -> stringResource(R.string.result_rolling)
        record == null -> stringResource(R.string.result_ready)
        else -> rollResultText(record)
    }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        AnimatedContent(
            targetState = text,
            transitionSpec = {
                (fadeIn(animationSpec = tween(durationMillis = 200)) +
                    scaleIn(initialScale = 0.94f, animationSpec = tween(durationMillis = 200))) togetherWith
                    fadeOut(animationSpec = tween(durationMillis = 140))
            },
            label = "resultText",
        ) { value ->
            Text(
                text = value,
                style = MaterialTheme.typography.headlineSmall,
                color = if (state.isRolling) {
                    MaterialTheme.colorScheme.onSurfaceVariant
                } else {
                    MaterialTheme.colorScheme.onBackground
                },
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
            )
        }

        if (!state.isRolling && mode == DiceMode.TWO_DICE && record != null) {
            Spacer(Modifier.height(10.dp))
            TotalChip(total = record.total)
        }
    }
}

@Composable
private fun TotalChip(total: Int, modifier: Modifier = Modifier) {
    val shape = RoundedCornerShape(percent = 50)
    Box(
        modifier = modifier
            .clip(shape)
            .background(AppGradients.chip())
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.45f),
                shape = shape,
            )
            .padding(horizontal = 16.dp, vertical = 6.dp),
    ) {
        Text(
            text = stringResource(R.string.result_total_chip, total),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.secondary,
            fontWeight = FontWeight.Bold,
        )
    }
}

/** "You rolled a 4!" in single dice mode, "You rolled 4 + 5 = 9!" in two dice mode. */
@Composable
fun rollResultText(record: RollRecord): String = when (record.faces.size) {
    1 -> stringResource(R.string.result_single, record.faces[0])
    else -> stringResource(R.string.result_double, record.faces[0], record.faces[1], record.total)
}
