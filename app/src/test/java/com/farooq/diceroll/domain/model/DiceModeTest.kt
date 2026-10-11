package com.farooq.diceroll.domain.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class DiceModeTest {

    @Test
    fun `single dice mode rolls one dice`() {
        assertEquals(1, DiceMode.ONE_DICE.diceCount)
        assertEquals(listOf(1), DiceMode.ONE_DICE.initialFaces)
    }

    @Test
    fun `two dice mode rolls two dice`() {
        assertEquals(2, DiceMode.TWO_DICE.diceCount)
        assertEquals(listOf(1, 1), DiceMode.TWO_DICE.initialFaces)
    }

    @Test
    fun `the dice shown before the first roll are always valid`() {
        DiceMode.entries.forEach { mode ->
            assertEquals(mode.diceCount, mode.initialFaces.size)
            assertTrue(DiceFaces.areValid(mode.initialFaces))
        }
    }

    @Test
    fun `a saved mode can be read back`() {
        DiceMode.entries.forEach { mode ->
            assertEquals(mode, DiceMode.fromStorageKey(mode.storageKey))
        }
    }

    @Test
    fun `unknown or missing saved values fall back to the default mode`() {
        assertEquals(DiceMode.DEFAULT, DiceMode.fromStorageKey(null))
        assertEquals(DiceMode.DEFAULT, DiceMode.fromStorageKey(""))
        assertEquals(DiceMode.DEFAULT, DiceMode.fromStorageKey("three"))
        assertEquals(DiceMode.ONE_DICE, DiceMode.DEFAULT)
    }

    @Test
    fun `every mode has its own storage key`() {
        val keys = DiceMode.entries.map { it.storageKey }
        assertEquals(keys.size, keys.distinct().size)
    }
}
