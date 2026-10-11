package com.farooq.diceroll.domain.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Every dice face must show exactly as many dots as its value, and no other value than 1..6
 * may ever be accepted.
 */
class DiceFacesTest {

    @Test
    fun `a face shows exactly as many pips as its value`() {
        DiceFaces.ALL.forEach { face ->
            assertEquals("face $face", face, DiceFaces.pipCount(face))
        }
    }

    @Test
    fun `every face from 1 to 6 has a pip layout`() {
        assertEquals(DiceFaces.ALL, DiceFaces.PIP_LAYOUT.keys.sorted())
        assertEquals(6, DiceFaces.PIP_LAYOUT.size)
    }

    @Test
    fun `the face with one pip shows it in the middle`() {
        val pips = DiceFaces.pipsFor(1)
        assertEquals(listOf(DiceFaces.Pip(0.5f, 0.5f)), pips)
    }

    @Test
    fun `all pips stay inside the dice`() {
        DiceFaces.ALL.forEach { face ->
            DiceFaces.pipsFor(face).forEach { pip ->
                assertTrue("face $face, pip $pip", pip.x in 0f..1f)
                assertTrue("face $face, pip $pip", pip.y in 0f..1f)
            }
        }
    }

    @Test
    fun `the six pip face is drawn as two columns of three`() {
        val columns = DiceFaces.pipsFor(6).map { it.x }.distinct()
        val rows = DiceFaces.pipsFor(6).map { it.y }.distinct()
        assertEquals(2, columns.size)
        assertEquals(3, rows.size)
    }

    @Test
    fun `symmetric faces are symmetric`() {
        // A real dice looks the same when turned upside down: 2, 3, 4, 5 and 6 are symmetric.
        listOf(2, 3, 4, 5, 6).forEach { face ->
            val mirrored = DiceFaces.pipsFor(face).map { DiceFaces.Pip(1f - it.x, 1f - it.y) }
            assertEquals("face $face should be point symmetric", DiceFaces.pipsFor(face).toSet(), mirrored.toSet())
        }
    }

    @Test
    fun `faces outside 1 to 6 are rejected`() {
        listOf(-1, 0, 7, 12, Int.MIN_VALUE, Int.MAX_VALUE).forEach { face ->
            assertFalse("$face must not be a valid face", DiceFaces.isValid(face))
            assertThrows(IllegalArgumentException::class.java) { DiceFaces.pipsFor(face) }
        }
    }

    @Test
    fun `valid faces are accepted`() {
        DiceFaces.ALL.forEach { face -> assertTrue(DiceFaces.isValid(face)) }
        assertEquals(1, DiceFaces.MIN)
        assertEquals(6, DiceFaces.MAX)
        assertEquals(6, DiceFaces.COUNT)
    }

    @Test
    fun `a list of faces is only valid when every single face is valid`() {
        assertTrue(DiceFaces.areValid(listOf(1)))
        assertTrue(DiceFaces.areValid(listOf(6, 6)))
        assertFalse("an empty list is not a roll", DiceFaces.areValid(emptyList()))
        assertFalse(DiceFaces.areValid(listOf(0)))
        assertFalse(DiceFaces.areValid(listOf(7)))
        assertFalse(DiceFaces.areValid(listOf(3, 9)))
    }
}
