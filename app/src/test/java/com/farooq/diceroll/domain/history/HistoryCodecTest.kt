package com.farooq.diceroll.domain.history

import com.farooq.diceroll.domain.model.DiceMode
import com.farooq.diceroll.domain.model.RollRecord
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class HistoryCodecTest {

    private fun single(face: Int, timestamp: Long = 1_735_689_600_000L) =
        RollRecord(timestamp, DiceMode.ONE_DICE, listOf(face))

    private fun double(first: Int, second: Int, timestamp: Long = 1_735_689_601_000L) =
        RollRecord(timestamp, DiceMode.TWO_DICE, listOf(first, second))

    @Test
    fun `history survives saving and loading`() {
        val records = listOf(
            double(4, 2),
            single(6),
            single(1, timestamp = 1_735_689_000_000L),
        )
        assertEquals(records, HistoryCodec.decode(HistoryCodec.encode(records)))
    }

    @Test
    fun `an empty or missing history is simply empty`() {
        assertTrue(HistoryCodec.decode(null).isEmpty())
        assertTrue(HistoryCodec.decode("").isEmpty())
        assertTrue(HistoryCodec.decode("   ").isEmpty())
        assertEquals("", HistoryCodec.encode(emptyList()))
    }

    @Test
    fun `broken entries are skipped instead of crashing the app`() {
        val saved = """
            |1735689600000|one|5
            |this is not a roll
            |1735689601000|three|4
            |1735689602000|two|4
            |1735689603000|one|0
            |1735689604000|one|7
            |1735689605000|one|notanumber
            |1735689606000|two|3,9
            |1735689607000|one|2
        """.trimMargin()
        val records = HistoryCodec.decode(saved)
        assertEquals(2, records.size)
        assertEquals(listOf(5), records[0].faces)
        assertEquals(listOf(2), records[1].faces)
    }

    @Test
    fun `a new roll is added to the front and keeps only the last twenty rolls`() {
        var history = emptyList<RollRecord>()
        repeat(HistoryCodec.HISTORY_LIMIT + 5) { index ->
            history = HistoryCodec.addNewest(history, single(face = (index % 6) + 1, timestamp = index.toLong()))
        }
        assertEquals(HistoryCodec.HISTORY_LIMIT, history.size)
        // Newest first
        assertEquals(24L, history.first().timestamp)
        assertEquals(5L, history.last().timestamp)
    }

    @Test
    fun `loading never returns more than twenty rolls`() {
        val records = (0 until HistoryCodec.HISTORY_LIMIT + 10).map { single(3, timestamp = it.toLong()) }
        assertEquals(HistoryCodec.HISTORY_LIMIT, HistoryCodec.decode(HistoryCodec.encode(records)).size)
    }

    @Test
    fun `the saved text is exactly the documented format`() {
        val saved = HistoryCodec.encode(listOf(single(5, timestamp = 1735689600000L), double(4, 2, 1735689601000L)))
        assertEquals("1735689600000|one|5\n1735689601000|two|4,2", saved)
    }

    @Test
    fun `newest roll is first in the list`() {
        val older = single(1, timestamp = 100L)
        val newer = single(6, timestamp = 200L)
        val history = HistoryCodec.addNewest(listOf(older), newer)
        assertEquals(listOf(newer, older), history)
    }
}
