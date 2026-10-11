package com.farooq.diceroll.domain.feedback

/**
 * Plays the short dice sounds. Implemented on Android by
 * [com.farooq.diceroll.platform.SoundPoolController] and replaced by a recording fake in tests.
 */
interface SoundController {

    /** Called once when a roll starts (the dice rattling over the table). */
    fun playRoll()

    /** Called when the dice stop (a soft click). */
    fun playLand()
}
