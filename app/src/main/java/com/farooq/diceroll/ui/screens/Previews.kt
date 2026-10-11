package com.farooq.diceroll.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.farooq.diceroll.domain.model.AppSettings
import com.farooq.diceroll.domain.model.DiceMode
import com.farooq.diceroll.domain.model.RollRecord
import com.farooq.diceroll.domain.model.ThemeMode
import com.farooq.diceroll.ui.components.AppBackground
import com.farooq.diceroll.ui.components.AppHeader
import com.farooq.diceroll.ui.roll.RollState
import com.farooq.diceroll.ui.theme.DiceRollTheme

/**
 * Previews for Android Studio. Open this file and switch to the "Split" or "Design" view to see the
 * app without running it on a device - very handy while changing colours or spacing.
 *
 * (Previews are only used by the IDE; they are not part of the app that is installed.)
 */
private val previewHistory = listOf(
    RollRecord(timestamp = 1_735_689_600_000L, mode = DiceMode.TWO_DICE, faces = listOf(4, 5)),
    RollRecord(timestamp = 1_735_689_500_000L, mode = DiceMode.TWO_DICE, faces = listOf(2, 2)),
    RollRecord(timestamp = 1_735_689_400_000L, mode = DiceMode.ONE_DICE, faces = listOf(6)),
    RollRecord(timestamp = 1_735_689_300_000L, mode = DiceMode.ONE_DICE, faces = listOf(3)),
)

@Preview(name = "Roll - dark", showBackground = true, backgroundColor = 0xFF101736, widthDp = 400, heightDp = 900)
@Composable
fun RollScreenDarkPreview() {
    DiceRollTheme(themeMode = ThemeMode.DARK) {
        AppBackground {
            Column(modifier = Modifier.fillMaxSize()) {
                AppHeader(showSettingsButton = true, onOpenSettings = {})
                RollScreen(
                    state = RollState(faces = listOf(3), isRolling = false, lastRecord = previewHistory.last()),
                    mode = DiceMode.ONE_DICE,
                    history = previewHistory,
                    onRoll = {},
                    onModeSelected = {},
                    onViewHistory = {},
                )
            }
        }
    }
}

@Preview(name = "Roll - two dice, dark", showBackground = true, backgroundColor = 0xFF101736, widthDp = 400, heightDp = 900)
@Composable
fun RollScreenTwoDicePreview() {
    DiceRollTheme(themeMode = ThemeMode.DARK) {
        AppBackground {
            Column(modifier = Modifier.fillMaxSize()) {
                AppHeader(showSettingsButton = true, onOpenSettings = {})
                RollScreen(
                    state = RollState(faces = listOf(4, 5), isRolling = false, lastRecord = previewHistory.first()),
                    mode = DiceMode.TWO_DICE,
                    history = previewHistory,
                    onRoll = {},
                    onModeSelected = {},
                    onViewHistory = {},
                )
            }
        }
    }
}

@Preview(name = "Roll - light", showBackground = true, backgroundColor = 0xFFF7F9FF, widthDp = 400, heightDp = 900)
@Composable
fun RollScreenLightPreview() {
    DiceRollTheme(themeMode = ThemeMode.LIGHT) {
        AppBackground {
            Column(modifier = Modifier.fillMaxSize()) {
                AppHeader(showSettingsButton = true, onOpenSettings = {})
                RollScreen(
                    state = RollState(faces = listOf(6), isRolling = false, lastRecord = previewHistory[2]),
                    mode = DiceMode.ONE_DICE,
                    history = previewHistory,
                    onRoll = {},
                    onModeSelected = {},
                    onViewHistory = {},
                )
            }
        }
    }
}

@Preview(name = "History - dark", showBackground = true, backgroundColor = 0xFF101736, widthDp = 400, heightDp = 900)
@Composable
fun HistoryScreenPreview() {
    DiceRollTheme(themeMode = ThemeMode.DARK) {
        AppBackground {
            Column(modifier = Modifier.fillMaxSize()) {
                AppHeader(showSettingsButton = true, onOpenSettings = {})
                HistoryScreen(history = previewHistory, onClearHistory = {})
            }
        }
    }
}

@Preview(name = "Settings - dark", showBackground = true, backgroundColor = 0xFF101736, widthDp = 400, heightDp = 900)
@Composable
fun SettingsScreenPreview() {
    DiceRollTheme(themeMode = ThemeMode.DARK) {
        AppBackground {
            Column(modifier = Modifier.fillMaxSize()) {
                AppHeader(showSettingsButton = false, onOpenSettings = {})
                Spacer(Modifier.height(4.dp))
                SettingsScreen(
                    settings = AppSettings(themeMode = ThemeMode.DARK),
                    rollCount = previewHistory.size,
                    onSoundChange = {},
                    onVibrationChange = {},
                    onThemeChange = {},
                    onClearHistory = {},
                )
            }
        }
    }
}
