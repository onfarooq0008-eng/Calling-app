package com.farooq.diceroll.domain.history

import com.farooq.diceroll.domain.model.DiceFaces
import com.farooq.diceroll.domain.model.RollRecord

/**
 * Small statistics shown at the top of the History screen.
 * All values are calculated from the rolls that are saved on the device - nothing is invented.
 */
data class HistoryStats(
    /** How many rolls the statistics are based on. */
    val rollCount: Int = 0,
    /** Average of the totals, e.g. 7.4 for a mix of two dice rolls. */
    val averageTotal: Double = 0.0,
    /** The face that came up most often, or `null` when there is nothing to show yet. */
    val mostRolledFace: Int? = null,
) {
    /** `true` when there is at least one roll to show statistics for. */
    val hasData: Boolean
        get() = rollCount > 0

    companion object {
        /** Calculates the statistics for a list of rolls. */
        fun from(records: List<RollRecord>): HistoryStats {
            if (records.isEmpty()) return HistoryStats()

            val countsPerFace = IntArray(DiceFaces.COUNT)
            records.forEach { record ->
                record.faces.forEach { face ->
                    countsPerFace[face - DiceFaces.MIN]++
                }
            }
            val highestCount = countsPerFace.maxOrNull() ?: 0
            val mostRolledFace = if (highestCount == 0) {
                null
            } else {
                // indexOf returns the first match, so ties always pick the lower face.
                countsPerFace.indexOf(highestCount) + DiceFaces.MIN
            }

            return HistoryStats(
                rollCount = records.size,
                averageTotal = records.map { it.total }.average(),
                mostRolledFace = mostRolledFace,
            )
        }
    }
}
