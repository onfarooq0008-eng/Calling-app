package com.farooq.diceroll.data.history

import com.farooq.diceroll.domain.model.RollRecord
import kotlinx.coroutines.flow.Flow

/**
 * Where the app keeps the recent rolls.
 *
 * The ViewModel only knows this interface, so tests can swap in a fake store
 * (see `FakeHistoryStore`).
 */
interface HistoryStore {

    /** The saved rolls, newest first. Emits again whenever the history changes. */
    val history: Flow<List<RollRecord>>

    /** Saves a roll at the top of the list and drops the oldest one when the limit is reached. */
    suspend fun add(record: RollRecord)

    /** Deletes every saved roll. */
    suspend fun clear()
}
