package com.farooq.diceroll.testutil

import com.farooq.diceroll.data.history.HistoryStore
import com.farooq.diceroll.data.settings.SettingsStore
import com.farooq.diceroll.domain.feedback.HapticController
import com.farooq.diceroll.domain.feedback.SoundController
import com.farooq.diceroll.domain.history.HistoryCodec
import com.farooq.diceroll.domain.model.AppSettings
import com.farooq.diceroll.domain.model.DiceMode
import com.farooq.diceroll.domain.model.RollRecord
import com.farooq.diceroll.domain.model.ThemeMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

/**
 * Test doubles: they behave like the real stores, but live in memory and let the tests look at
 * what the ViewModel asked them to save. This is why the app's storage lives behind interfaces.
 */
class FakeSettingsStore(initial: AppSettings = AppSettings()) : SettingsStore {

    val state = MutableStateFlow(initial)
    override val settings: Flow<AppSettings> = state

    val savedSound = mutableListOf<Boolean>()
    val savedVibration = mutableListOf<Boolean>()
    val savedThemes = mutableListOf<ThemeMode>()
    val savedModes = mutableListOf<DiceMode>()

    override suspend fun setSoundEnabled(enabled: Boolean) {
        savedSound += enabled
        state.value = state.value.copy(soundEnabled = enabled)
    }

    override suspend fun setVibrationEnabled(enabled: Boolean) {
        savedVibration += enabled
        state.value = state.value.copy(vibrationEnabled = enabled)
    }

    override suspend fun setThemeMode(themeMode: ThemeMode) {
        savedThemes += themeMode
        state.value = state.value.copy(themeMode = themeMode)
    }

    override suspend fun setDiceMode(diceMode: DiceMode) {
        savedModes += diceMode
        state.value = state.value.copy(diceMode = diceMode)
    }
}

class FakeHistoryStore(initial: List<RollRecord> = emptyList()) : HistoryStore {

    val state = MutableStateFlow(initial)
    override val history: Flow<List<RollRecord>> = state

    val added = mutableListOf<RollRecord>()
    var clearCount = 0
        private set

    override suspend fun add(record: RollRecord) {
        added += record
        state.value = HistoryCodec.addNewest(state.value, record)
    }

    override suspend fun clear() {
        clearCount++
        state.value = emptyList()
    }
}

class RecordingSoundController : SoundController {
    var rollSounds = 0
        private set
    var landSounds = 0
        private set

    override fun playRoll() {
        rollSounds++
    }

    override fun playLand() {
        landSounds++
    }
}

class RecordingHapticController : HapticController {
    var rollFeedbacks = 0
        private set
    var landFeedbacks = 0
        private set

    override fun rollFeedback() {
        rollFeedbacks++
    }

    override fun landFeedback() {
        landFeedbacks++
    }
}
