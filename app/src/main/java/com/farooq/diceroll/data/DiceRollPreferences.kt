package com.farooq.diceroll.data

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

/**
 * Names of the values that are stored on the device with DataStore.
 *
 * Both the settings and the roll history live in the same small preferences file
 * (`dice_roll.preferences_pb` inside the app's private storage).
 *
 * Never rename a key: old installations would lose their saved values.
 */
internal object DiceRollPreferences {

    /** Sound on/off. */
    val SOUND_ENABLED = booleanPreferencesKey("sound_enabled")

    /** Vibration on/off. */
    val VIBRATION_ENABLED = booleanPreferencesKey("vibration_enabled")

    /** Light / dark / system (see ThemeMode). */
    val THEME_MODE = stringPreferencesKey("theme_mode")

    /** One dice or two dice (see DiceMode). */
    val DICE_MODE = stringPreferencesKey("dice_mode")

    /** The roll history, encoded by HistoryCodec. */
    val ROLL_HISTORY = stringPreferencesKey("roll_history")
}
