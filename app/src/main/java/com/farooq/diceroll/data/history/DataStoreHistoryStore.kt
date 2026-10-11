package com.farooq.diceroll.data.history

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import com.farooq.diceroll.data.DiceRollPreferences
import com.farooq.diceroll.domain.history.HistoryCodec
import com.farooq.diceroll.domain.model.RollRecord
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

/**
 * Saves the last [HistoryCodec.HISTORY_LIMIT] rolls with Jetpack DataStore.
 * No account, no internet connection, no database - just a small file on the device.
 */
class DataStoreHistoryStore(
    private val dataStore: DataStore<Preferences>,
) : HistoryStore {

    override val history: Flow<List<RollRecord>> = dataStore.data
        .catch { error ->
            if (error is IOException) emit(emptyPreferences()) else throw error
        }
        .map { preferences -> HistoryCodec.decode(preferences[DiceRollPreferences.ROLL_HISTORY]) }

    override suspend fun add(record: RollRecord) {
        // Reading and writing inside the same edit block keeps the list consistent.
        dataStore.edit { preferences ->
            val saved = HistoryCodec.decode(preferences[DiceRollPreferences.ROLL_HISTORY])
            val updated = HistoryCodec.addNewest(saved, record)
            preferences[DiceRollPreferences.ROLL_HISTORY] = HistoryCodec.encode(updated)
        }
    }

    override suspend fun clear() {
        dataStore.edit { preferences -> preferences.remove(DiceRollPreferences.ROLL_HISTORY) }
    }
}
