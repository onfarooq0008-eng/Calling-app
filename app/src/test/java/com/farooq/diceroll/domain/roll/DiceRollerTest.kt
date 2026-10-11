package com.farooq.diceroll.domain.roll

import com.farooq.diceroll.domain.model.DiceFaces
import com.farooq.diceroll.domain.model.DiceMode
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.random.Random

/**
 * The most important tests in the app: a dice must never show 0, 7 or anything else outside 1..6,
 * and every face must really have the same chance.
 */
class DiceRollerTest {

    private val roller = DiceRoller(Random(seed = 20240815))

    @Test
    fun `a single roll is always between 1 and 6`() {
        repeat(20_000) {
            val face = roller.rollOne()
            assertTrue("rolled $face", face in DiceFaces.MIN..DiceFaces.MAX)
        }
    }

    @Test
    fun `every face shows up and none is favoured too much`() {
        val rolls = 60_000
        val counts = IntArray(DiceFaces.COUNT)
        repeat(rolls) { counts[roller.rollOne() - DiceFaces.MIN]++ }

        val expected = rolls / DiceFaces.COUNT.toDouble()
        counts.forEachIndexed { index, count ->
            val face = index + DiceFaces.MIN
            assertTrue("face $face never came up", count > 0)
            // Generous tolerance: with a real random generator this never fails,
            // but a broken generator (always 6, alternating, ...) is caught immediately.
            assertTrue(
                "face $face came up $count times, expected around $expected",
                count > expected * 0.9 && count < expected * 1.1,
            )
        }
    }

    @Test
    fun `two dice are rolled independently and always add up to 2 to 12`() {
        repeat(5_000) {
            val faces = roller.roll(DiceMode.TWO_DICE.diceCount)
            assertEquals(2, faces.size)
            assertTrue(DiceFaces.areValid(faces))
            assertTrue("total of $faces", faces.sum() in 2..12)
        }
    }

    @Test
    fun `rolling without dice is not possible`() {
        assertThrows(IllegalArgumentException::class.java) { roller.roll(0) }
        assertThrows(IllegalArgumentException::class.java) { roller.roll(-1) }
    }

    @Test
    fun `the animation shows the requested number of frames`() {
        val frames = roller.rollFrames(frameCount = RollTiming.FRAME_COUNT, finalFaces = listOf(4))
        assertEquals(RollTiming.FRAME_COUNT, frames.size)
    }

    @Test
    fun `the animation always ends on the real result`() {
        repeat(200) {
            DiceFaces.ALL.forEach { face ->
                val frames = roller.rollFrames(RollTiming.FRAME_COUNT, listOf(face))
                assertEquals(listOf(face), frames.last())
            }
            val twoDice = listOf(2, 5)
            assertEquals(twoDice, roller.rollFrames(RollTiming.FRAME_COUNT, twoDice).last())
        }
    }

    @Test
    fun `every animation frame is a valid dice roll`() {
        repeat(100) {
            val frames = roller.rollFrames(RollTiming.FRAME_COUNT, listOf(3))
            frames.forEach { frame ->
                assertEquals(1, frame.size)
                assertTrue(DiceFaces.areValid(frame))
            }
            val twoDiceFrames = roller.rollFrames(RollTiming.FRAME_COUNT, listOf(6, 1))
            twoDiceFrames.forEach { frame ->
                assertEquals(2, frame.size)
                assertTrue(DiceFaces.areValid(frame))
            }
        }
    }

    @Test
    fun `the dice never appear frozen during the animation`() {
        repeat(100) {
            val frames = roller.rollFrames(RollTiming.FRAME_COUNT, listOf(2, 2))
            frames.zipWithNext().forEachIndexed { index, (current, next) ->
                assertNotEquals("frames $index and ${index + 1} look the same", current, next)
            }
        }
    }

    @Test
    fun `the landing on the final result is always visible`() {
        repeat(100) {
            val finalFaces = listOf(5, 5)
            val frames = roller.rollFrames(RollTiming.FRAME_COUNT, finalFaces)
            assertNotEquals("the frame before the result must differ from it", finalFaces, frames[frames.size - 2])
        }
    }

    @Test
    fun `a single frame animation is just the result`() {
        assertEquals(listOf(6), roller.rollFrames(frameCount = 1, finalFaces = listOf(6)).single())
    }

    @Test
    fun `nonsense animation requests are rejected`() {
        assertThrows(IllegalArgumentException::class.java) {
            roller.rollFrames(frameCount = 0, finalFaces = listOf(1))
        }
        assertThrows(IllegalArgumentException::class.java) {
            roller.rollFrames(frameCount = 5, finalFaces = listOf(0))
        }
        assertThrows(IllegalArgumentException::class.java) {
            roller.rollFrames(frameCount = 5, finalFaces = emptyList())
        }
    }

    @Test
    fun `the same seed always produces the same rolls`() {
        val first = DiceRoller(Random(7)).roll(10)
        val second = DiceRoller(Random(7)).roll(10)
        assertEquals(first, second)
    }

    @Test
    fun `a roll lasts 900 milliseconds`() {
        assertEquals(12, RollTiming.FRAME_COUNT)
        assertEquals(75L, RollTiming.FRAME_INTERVAL_MS)
        assertEquals(900L, RollTiming.DURATION_MS)
        assertEquals(RollTiming.FRAME_INTERVAL_MS * RollTiming.FRAME_COUNT, RollTiming.DURATION_MS)
    }
}
