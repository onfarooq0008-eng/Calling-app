package com.farooq.diceroll.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.farooq.diceroll.data.history.DataStoreHistoryStore
import com.farooq.diceroll.data.history.HistoryStore
import com.farooq.diceroll.data.settings.DataStoreSettingsStore
import com.farooq.diceroll.data.settings.SettingsStore
import com.farooq.diceroll.domain.feedback.HapticController
import com.farooq.diceroll.domain.feedback.SoundController
import com.farooq.diceroll.domain.roll.DiceRoller
import com.farooq.diceroll.platform.SoundPoolController
import com.farooq.diceroll.platform.VibratorController

/**
 * The app's "wiring": every object that should exist only once is created here and handed to
 * whoever needs it.
 *
 * This keeps the ViewModel free of Android details (it only sees the small interfaces in the
 * `domain` package), which is what makes the app testable.
 */
class AppContainer(context: Context) {

    private val appContext: Context = context.applicationContext

    private val dataStore: DataStore<Preferences> = appContext.diceRollDataStore

    /** Where the settings are stored. */
    val settingsStore: SettingsStore = DataStoreSettingsStore(dataStore)

    /** Where the roll history is stored. */
    val historyStore: HistoryStore = DataStoreHistoryStore(dataStore)

    /** The random dice generator. */
    val diceRoller: DiceRoller = DiceRoller()

    /** Sound effects (no-op while the phone is on silent). */
    val soundController: SoundController = SoundPoolController(appContext)

    /** Vibration feedback (no-op on devices without a vibrator). */
    val hapticController: HapticController = VibratorController(appContext)
}

/**
 * The single DataStore file used by the app. Declaring it here makes sure Android creates it
 * exactly once, no matter how many screens ask for it.
 */
private val Context.diceRollDataStore by preferencesDataStore(name = "dice_roll")
