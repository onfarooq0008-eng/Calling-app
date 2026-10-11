package com.farooq.diceroll.ui.roll

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.farooq.diceroll.data.history.HistoryStore
import com.farooq.diceroll.data.settings.SettingsStore
import com.farooq.diceroll.di.AppContainer
import com.farooq.diceroll.domain.feedback.HapticController
import com.farooq.diceroll.domain.feedback.SoundController
import com.farooq.diceroll.domain.model.AppSettings
import com.farooq.diceroll.domain.model.DiceMode
import com.farooq.diceroll.domain.model.RollRecord
import com.farooq.diceroll.domain.model.ThemeMode
import com.farooq.diceroll.domain.roll.DiceRoller
import com.farooq.diceroll.domain.roll.RollTiming
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * The brain of the app: it rolls the dice, keeps the animation running, saves the history and
 * remembers the settings.
 *
 * The ViewModel survives screen rotations (Android keeps it while the screen is recreated), so a
 * roll in progress is not interrupted when the phone is turned.
 *
 * It has no idea that Compose or Android exists: it only talks to the interfaces in the `domain`
 * and `data` packages, which is why every rule in this class can be unit tested on the JVM.
 */
class RollViewModel(
    private val settingsStore: SettingsStore,
    private val historyStore: HistoryStore,
    private val diceRoller: DiceRoller,
    private val soundController: SoundController,
    private val hapticController: HapticController,
    /** Where the current time comes from - replaceable so tests get predictable timestamps. */
    private val clock: () -> Long = System::currentTimeMillis,
) : ViewModel() {

    private val _rollState = MutableStateFlow(RollState())

    /** What the dice are doing right now. */
    val rollState: StateFlow<RollState> = _rollState.asStateFlow()

    private val _settings = MutableStateFlow(AppSettings())

    /** The saved preferences (sound, vibration, theme, dice mode). */
    val settings: StateFlow<AppSettings> = _settings.asStateFlow()

    private val _history = MutableStateFlow<List<RollRecord>>(emptyList())

    /** The stored rolls, newest first (at most [com.farooq.diceroll.domain.history.HistoryCodec.HISTORY_LIMIT]). */
    val history: StateFlow<List<RollRecord>> = _history.asStateFlow()

    /** The running roll, so it can be cancelled when the player switches dice mode. */
    private var rollJob: Job? = null

    init {
        // Keep the UI in sync with the settings that are stored on the device.
        viewModelScope.launch {
            settingsStore.settings.collect { stored ->
                _settings.value = stored
                val diceCountChanged = _rollState.value.faces.size != stored.diceMode.diceCount
                if (diceCountChanged) {
                    // e.g. the app was closed in two dice mode: show two dice again.
                    cancelRoll()
                    _rollState.update { current ->
                        current.copy(faces = stored.diceMode.initialFaces, isRolling = false)
                    }
                }
            }
        }
        // Keep the UI in sync with the stored roll history.
        viewModelScope.launch {
            historyStore.history.collect { records -> _history.value = records }
        }
    }

    /**
     * Rolls the dice: picks a random result, then plays the tumbling animation and finally
     * saves the roll.
     *
     * Tapping the dice and tapping the ROLL DICE button both end up here.
     */
    fun roll() {
        // A roll that is already running wins: taps during the animation are ignored, so the
        // player can never roll twice by accident.
        if (_rollState.value.isRolling) return

        val mode = _settings.value.diceMode

        // The result is decided *before* the animation starts. That is why the dice always land
        // on exactly the value that was rolled, and why the animation cannot change the result.
        val finalFaces = diceRoller.roll(mode.diceCount)

        // Set the "rolling" flag right away (not inside the coroutine below), so even two taps
        // in the same frame cannot start two rolls.
        _rollState.update { current -> current.copy(isRolling = true) }

        if (_settings.value.soundEnabled) soundController.playRoll()
        if (_settings.value.vibrationEnabled) hapticController.rollFeedback()

        rollJob = viewModelScope.launch {
            diceRoller.rollFrames(RollTiming.FRAME_COUNT, finalFaces).forEach { frame ->
                _rollState.update { current -> current.copy(faces = frame) }
                delay(RollTiming.FRAME_INTERVAL_MS)
            }
            finishRoll(mode = mode, faces = finalFaces)
        }
    }

    /** Ends the animation on the real result, plays the landing feedback and saves the roll. */
    private suspend fun finishRoll(mode: DiceMode, faces: List<Int>) {
        val record = RollRecord(timestamp = clock(), mode = mode, faces = faces)
        _rollState.update { current ->
            current.copy(faces = faces, isRolling = false, lastRecord = record)
        }
        if (_settings.value.soundEnabled) soundController.playLand()
        if (_settings.value.vibrationEnabled) hapticController.landFeedback()
        historyStore.add(record)
    }

    /**
     * Switches between one and two dice. The choice is saved, the current roll (if any) is
     * stopped and the dice are reset to a valid face.
     */
    fun setMode(mode: DiceMode) {
        if (_settings.value.diceMode == mode) return

        // Update the UI immediately; the value is written to storage in the background.
        _settings.update { current -> current.copy(diceMode = mode) }
        if (_rollState.value.faces.size != mode.diceCount) {
            cancelRoll()
            _rollState.update { current ->
                current.copy(faces = mode.initialFaces, isRolling = false)
            }
        }
        viewModelScope.launch { settingsStore.setDiceMode(mode) }
    }

    /** Turns the sound effect on or off. */
    fun setSoundEnabled(enabled: Boolean) {
        _settings.update { current -> current.copy(soundEnabled = enabled) }
        viewModelScope.launch { settingsStore.setSoundEnabled(enabled) }
    }

    /** Turns the vibration feedback on or off. */
    fun setVibrationEnabled(enabled: Boolean) {
        _settings.update { current -> current.copy(vibrationEnabled = enabled) }
        viewModelScope.launch { settingsStore.setVibrationEnabled(enabled) }
    }

    /** Switches between the light theme, the dark theme and the phone's own setting. */
    fun setThemeMode(themeMode: ThemeMode) {
        _settings.update { current -> current.copy(themeMode = themeMode) }
        viewModelScope.launch { settingsStore.setThemeMode(themeMode) }
    }

    /**
     * Deletes the saved roll history (the app asks for confirmation before calling this).
     * The result of the last roll stays on screen - only the history is emptied.
     */
    fun clearHistory() {
        viewModelScope.launch { historyStore.clear() }
    }

    private fun cancelRoll() {
        rollJob?.cancel()
        rollJob = null
    }

    companion object {
        /** Builds the ViewModel with the app's shared objects (see [AppContainer]). */
        fun factory(container: AppContainer): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                RollViewModel(
                    settingsStore = container.settingsStore,
                    historyStore = container.historyStore,
                    diceRoller = container.diceRoller,
                    soundController = container.soundController,
                    hapticController = container.hapticController,
                )
            }
        }
    }
}
