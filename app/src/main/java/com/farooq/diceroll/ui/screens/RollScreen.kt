package com.farooq.diceroll.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.farooq.diceroll.domain.model.DiceMode
import com.farooq.diceroll.domain.model.RollRecord
import com.farooq.diceroll.ui.components.DiceStage
import com.farooq.diceroll.ui.components.ModeSelector
import com.farooq.diceroll.ui.components.RecentRollsCard
import com.farooq.diceroll.ui.components.ResultBanner
import com.farooq.diceroll.ui.components.RollButton
import com.farooq.diceroll.ui.roll.RollState

/**
 * The main screen: the dice, the result, the ROLL DICE button, the dice mode switch and the most
 * recent rolls.
 *
 * The screen is a plain function of the state it is given, which makes it easy to read and easy to
 * preview in Android Studio. It scrolls on short screens, so nothing is ever cut off.
 */
@Composable
fun RollScreen(
    state: RollState,
    mode: DiceMode,
    history: List<RollRecord>,
    onRoll: () -> Unit,
    onModeSelected: (DiceMode) -> Unit,
    onViewHistory: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(Modifier.height(4.dp))

        // The dice. Tap them to roll.
        DiceStage(
            faces = state.faces,
            isRolling = state.isRolling,
            onRoll = onRoll,
        )

        Spacer(Modifier.height(6.dp))

        ResultBanner(state = state, mode = mode)

        Spacer(Modifier.height(18.dp))

        RollButton(isRolling = state.isRolling, onClick = onRoll)

        Spacer(Modifier.height(22.dp))

        ModeSelector(selected = mode, onModeSelected = onModeSelected)

        Spacer(Modifier.height(24.dp))

        RecentRollsCard(
            records = history,
            onViewAll = onViewHistory,
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(Modifier.height(28.dp))
    }
}
