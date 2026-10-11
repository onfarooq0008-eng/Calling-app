package com.farooq.diceroll.domain.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test

class RollRecordTest {

    @Test
    fun `single dice total is the face value`() {
        DiceFaces.ALL.forEach { face ->
            val record = RollRecord(timestamp = 0L, mode = DiceMode.ONE_DICE, faces = listOf(face))
            assertEquals(face, record.total)
        }
    }

    @Test
    fun `two dice totals are always between 2 and 12`() {
        DiceFaces.ALL.forEach { first ->
            DiceFaces.ALL.forEach { second ->
                val record = RollRecord(0L, DiceMode.TWO_DICE, listOf(first, second))
                assertTrue("$first + $second = ${record.total}", record.total in 2..12)
                assertEquals(first + second, record.total)
            }
        }
    }

    @Test
    fun `a record cannot hold the wrong number of dice`() {
        assertThrows(IllegalArgumentException::class.java) {
            RollRecord(0L, DiceMode.ONE_DICE, listOf(1, 2))
        }
        assertThrows(IllegalArgumentException::class.java) {
            RollRecord(0L, DiceMode.TWO_DICE, listOf(3))
        }
        assertThrows(IllegalArgumentException::class.java) {
            RollRecord(0L, DiceMode.ONE_DICE, emptyList())
        }
    }

    @Test
    fun `a record cannot hold an impossible dice value`() {
        assertThrows(IllegalArgumentException::class.java) {
            RollRecord(0L, DiceMode.ONE_DICE, listOf(0))
        }
        assertThrows(IllegalArgumentException::class.java) {
            RollRecord(0L, DiceMode.ONE_DICE, listOf(7))
        }
        assertThrows(IllegalArgumentException::class.java) {
            RollRecord(0L, DiceMode.TWO_DICE, listOf(2, 9))
        }
    }
}
