package com.farooq.diceroll.domain.feedback

/**
 * Short vibration feedback. Implemented on Android by
 * [com.farooq.diceroll.platform.VibratorController] and replaced by a recording fake in tests.
 */
interface HapticController {

    /** Called once when a roll starts. */
    fun rollFeedback()

    /** Called when the dice stop. */
    fun landFeedback()
}
