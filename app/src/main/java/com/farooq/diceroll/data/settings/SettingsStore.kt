package com.farooq.diceroll.data.settings

import com.farooq.diceroll.domain.model.AppSettings
import com.farooq.diceroll.domain.model.DiceMode
import com.farooq.diceroll.domain.model.ThemeMode
import kotlinx.coroutines.flow.Flow

/**
 * Where the app keeps the player's preferences.
 *
 * The ViewModel only knows this interface, so tests can swap in a fake store
 * (see `FakeSettingsStore`) and every value can be checked on the JVM.
 */
interface SettingsStore {

    /** The saved settings. Emits again whenever something changes. */
    val settings: Flow<AppSettings>

    /** Saves the sound setting so it survives an app restart. */
    suspend fun setSoundEnabled(enabled: Boolean)

    /** Saves the vibration setting. */
    suspend fun setVibrationEnabled(enabled: Boolean)

    /** Saves the chosen theme. */
    suspend fun setThemeMode(themeMode: ThemeMode)

    /** Saves the dice mode, so the game starts in the same mode next time. */
    suspend fun setDiceMode(diceMode: DiceMode)
}
