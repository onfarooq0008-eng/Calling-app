package com.farooq.diceroll.data.settings

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import com.farooq.diceroll.data.DiceRollPreferences
import com.farooq.diceroll.domain.model.AppSettings
import com.farooq.diceroll.domain.model.DiceMode
import com.farooq.diceroll.domain.model.ThemeMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

/**
 * Saves the settings with Jetpack DataStore.
 *
 * DataStore writes to the app's private storage on a background thread, needs no permission,
 * works offline and keeps the values after the app is closed.
 */
class DataStoreSettingsStore(
    private val dataStore: DataStore<Preferences>,
) : SettingsStore {

    override val settings: Flow<AppSettings> = dataStore.data
        // A broken file must never crash the app: fall back to the defaults.
        .catch { error ->
            if (error is IOException) emit(emptyPreferences()) else throw error
        }
        .map { preferences -> preferences.toAppSettings() }

    override suspend fun setSoundEnabled(enabled: Boolean) {
        dataStore.edit { preferences -> preferences[DiceRollPreferences.SOUND_ENABLED] = enabled }
    }

    override suspend fun setVibrationEnabled(enabled: Boolean) {
        dataStore.edit { preferences -> preferences[DiceRollPreferences.VIBRATION_ENABLED] = enabled }
    }

    override suspend fun setThemeMode(themeMode: ThemeMode) {
        dataStore.edit { preferences -> preferences[DiceRollPreferences.THEME_MODE] = themeMode.storageKey }
    }

    override suspend fun setDiceMode(diceMode: DiceMode) {
        dataStore.edit { preferences -> preferences[DiceRollPreferences.DICE_MODE] = diceMode.storageKey }
    }
}

/** Reads [AppSettings] out of the stored preferences, using the defaults for missing values. */
private fun Preferences.toAppSettings(): AppSettings = AppSettings(
    soundEnabled = this[DiceRollPreferences.SOUND_ENABLED] ?: DEFAULT_SOUND,
    vibrationEnabled = this[DiceRollPreferences.VIBRATION_ENABLED] ?: DEFAULT_VIBRATION,
    themeMode = ThemeMode.fromStorageKey(this[DiceRollPreferences.THEME_MODE]),
    diceMode = DiceMode.fromStorageKey(this[DiceRollPreferences.DICE_MODE]),
)

private const val DEFAULT_SOUND = true
private const val DEFAULT_VIBRATION = true
