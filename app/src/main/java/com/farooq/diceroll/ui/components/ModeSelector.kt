package com.farooq.diceroll.ui.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.farooq.diceroll.R
import com.farooq.diceroll.domain.model.DiceMode
import com.farooq.diceroll.ui.theme.AppGradients
import com.farooq.diceroll.ui.theme.LocalIsDarkTheme

/**
 * The "1 Dice / 2 Dice" switch. The pill behind the selected option slides across with a short
 * animation, so the player immediately sees which mode is active.
 *
 * The selected mode is stored by the ViewModel, which means it is remembered the next time the app
 * is opened.
 */
@Composable
fun ModeSelector(
    selected: DiceMode,
    onModeSelected: (DiceMode) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    val isDark = LocalIsDarkTheme.current
    val options = DiceMode.entries
    val pillShape = RoundedCornerShape(percent = 50)

    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = stringResource(R.string.mode_label).uppercase(),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(Modifier.height(8.dp))

        BoxWithConstraints(
            modifier = Modifier
                .width(250.dp)
                .height(56.dp)
                .clip(pillShape)
                .background(
                    MaterialTheme.colorScheme.surfaceVariant.copy(alpha = if (isDark) 0.55f else 0.75f),
                )
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.45f),
                    shape = pillShape,
                )
                .padding(4.dp),
            contentAlignment = Alignment.Center,
        ) {
            val segmentWidth = maxWidth / options.size
            val selectedIndex = options.indexOf(selected).coerceAtLeast(0)
            val indicatorOffset by animateDpAsState(
                targetValue = segmentWidth * selectedIndex,
                animationSpec = tween(durationMillis = 260),
                label = "modeIndicator",
            )

            // The sliding pill behind the selected option.
            Box(
                modifier = Modifier
                    .offset(x = indicatorOffset)
                    .width(segmentWidth)
                    .fillMaxHeight()
                    .clip(pillShape)
                    .background(AppGradients.modeIndicator(isDark)),
            )

            Row(modifier = Modifier.fillMaxWidth()) {
                options.forEach { mode ->
                    val isSelected = mode == selected
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .clip(pillShape)
                            .clickable(enabled = enabled, role = Role.RadioButton) {
                                onModeSelected(mode)
                            },
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = stringResource(mode.labelRes()),
                            style = MaterialTheme.typography.titleSmall,
                            color = if (isSelected) {
                                MaterialTheme.colorScheme.onBackground
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant
                            },
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            textAlign = TextAlign.Center,
                            maxLines = 1,
                        )
                    }
                }
            }
        }
    }
}

/** The text shown on the mode switch for this mode. */
@Composable
fun DiceMode.labelRes(): Int = when (this) {
    DiceMode.ONE_DICE -> R.string.mode_one_dice
    DiceMode.TWO_DICE -> R.string.mode_two_dice
}
