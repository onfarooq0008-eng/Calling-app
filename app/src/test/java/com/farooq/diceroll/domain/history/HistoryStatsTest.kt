package com.farooq.diceroll.domain.history

import com.farooq.diceroll.domain.model.DiceMode
import com.farooq.diceroll.domain.model.RollRecord
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class HistoryStatsTest {

    private fun single(face: Int) = RollRecord(0L, DiceMode.ONE_DICE, listOf(face))
    private fun double(first: Int, second: Int) = RollRecord(0L, DiceMode.TWO_DICE, listOf(first, second))

    @Test
    fun `no rolls means no statistics`() {
        val stats = HistoryStats.from(emptyList())
        assertFalse(stats.hasData)
        assertEquals(0, stats.rollCount)
        assertEquals(0.0, stats.averageTotal, 0.0001)
        assertNull(stats.mostRolledFace)
    }

    @Test
    fun `rolls are counted and averaged`() {
        val stats = HistoryStats.from(listOf(double(4, 2), single(6), double(3, 3), single(1)))
        assertEquals(4, stats.rollCount)
        assertTrue(stats.hasData)
        // totals: 6, 6, 6, 1 -> 19 / 4
        assertEquals(19.0 / 4.0, stats.averageTotal, 0.0001)
    }

    @Test
    fun `the most rolled face wins`() {
        val stats = HistoryStats.from(listOf(single(3), single(3), single(5), single(1)))
        assertEquals(3, stats.mostRolledFace)
    }

    @Test
    fun `two dice rolls count every single dice`() {
        // faces: 6, 1, 6, 6 -> both dice of every roll are counted, so 6 wins
        val stats = HistoryStats.from(listOf(double(6, 1), double(6, 6)))
        assertEquals(6, stats.mostRolledFace)
        assertEquals(2, stats.rollCount)
        // totals 7 and 12 -> 9.5
        assertEquals(9.5, stats.averageTotal, 0.0001)
    }

    @Test
    fun `ties pick the lower face`() {
        val stats = HistoryStats.from(listOf(single(5), single(2)))
        assertEquals(2, stats.mostRolledFace)
    }
}
