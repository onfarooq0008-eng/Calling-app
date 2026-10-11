package com.farooq.diceroll.domain.roll

import com.farooq.diceroll.domain.model.DiceFaces
import kotlin.random.Random

/**
 * Rolls dice with a real random number generator.
 *
 * [Random] is Kotlin's random generator; every call to [rollOne] returns 1, 2, 3, 4, 5 or 6
 * with the same chance. Nothing weaker (and nothing out of range) can leave this class.
 *
 * The generator can be replaced, which makes the behaviour of the app testable:
 * `DiceRoller(Random(seed = 42))` always produces the same sequence.
 */
class DiceRoller(private val random: Random = Random.Default) {

    /** Rolls a single dice. The result is always between [DiceFaces.MIN] and [DiceFaces.MAX]. */
    fun rollOne(): Int = random.nextInt(from = DiceFaces.MIN, until = DiceFaces.MAX + 1)

    /**
     * Rolls [diceCount] dice independently, e.g. `[4, 5]` for two dice mode.
     * Every dice is rolled separately, so the total of two dice is always between 2 and 12.
     */
    fun roll(diceCount: Int): List<Int> {
        require(diceCount >= 1) { "At least one dice is needed, but was asked to roll $diceCount." }
        return List(diceCount) { rollOne() }
    }

    /**
     * Builds the faces that are shown *while* the dice tumble.
     *
     * The animation uses [frameCount] frames and always ends on [finalFaces], so the player sees the
     * real result of the roll. Two tricks make it look natural:
     *  - every frame is different from the frame after it (no "frozen" animation), and
     *  - the frame right before the last one is different from [finalFaces], so the landing is visible.
     *
     * The result of the roll is decided *before* the animation starts, which is why the dice always
     * end up showing exactly what [roll] returned.
     */
    fun rollFrames(frameCount: Int, finalFaces: List<Int>): List<List<Int>> {
        require(frameCount >= 1) { "frameCount must be at least 1, but was $frameCount." }
        require(DiceFaces.areValid(finalFaces)) { "Invalid final dice values: $finalFaces" }

        val frames = MutableList(frameCount) { randomFaces(finalFaces.size) }
        frames[frameCount - 1] = finalFaces

        // Walk backwards and make sure each frame differs from the following one.
        for (index in frameCount - 2 downTo 0) {
            frames[index] = makeDifferent(frames[index], frames[index + 1])
        }
        return frames
    }

    private fun randomFaces(diceCount: Int): List<Int> = List(diceCount) { rollOne() }

    /**
     * Returns [candidate] unless it is identical to [other]; in that case the first dice is
     * shifted by one (1->2, ..., 6->1) so the two frames can never look the same.
     */
    private fun makeDifferent(candidate: List<Int>, other: List<Int>): List<Int> {
        if (candidate != other) return candidate
        val shifted = candidate.toMutableList()
        shifted[0] = (shifted[0] % DiceFaces.MAX) + DiceFaces.MIN
        return shifted
    }
}
