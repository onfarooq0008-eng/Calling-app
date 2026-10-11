package com.farooq.diceroll.domain.roll

/**
 * Timings of the rolling animation, in one single place.
 *
 * The dice face changes every [FRAME_INTERVAL_MS] (that is what makes them look like they are
 * tumbling), [FRAME_COUNT] frames are shown in total, and the whole animation lasts
 * [DURATION_MS]. The rolling sound effect was made to be exactly as long as a roll.
 */
object RollTiming {

    /** How long one "tumbling" frame is shown. Smaller = faster, more frantic dice. */
    const val FRAME_INTERVAL_MS: Long = 75L

    /** How many frames a single roll shows. */
    const val FRAME_COUNT: Int = 12

    /** Total length of a roll: 12 frames x 75 ms = 900 ms. */
    const val DURATION_MS: Long = FRAME_INTERVAL_MS * FRAME_COUNT
}
