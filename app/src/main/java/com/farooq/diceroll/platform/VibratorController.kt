package com.farooq.diceroll.platform

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import com.farooq.diceroll.domain.feedback.HapticController

/**
 * Short vibration feedback for rolling dice.
 *
 * The patterns are gentle on purpose (a few very short buzzes), so they feel like a dice
 * tumbling over a table without hurting the battery. Devices without a vibrator simply do
 * nothing - the app never crashes and never asks for a permission dialog.
 */
class VibratorController(context: Context) : HapticController {

    private val vibrator: Vibrator? = resolveVibrator(context.applicationContext)

    override fun rollFeedback() = vibrate(ROLL_TIMINGS, ROLL_AMPLITUDES)

    override fun landFeedback() = vibrate(LAND_TIMINGS, LAND_AMPLITUDES)

    private fun vibrate(timings: LongArray, amplitudes: IntArray) {
        val device = vibrator ?: return
        if (!device.hasVibrator()) return

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            device.vibrate(VibrationEffect.createWaveform(timings, amplitudes, REPEAT_NONE))
        } else {
            // Android 7.x has no VibrationEffect; the pattern is still respected.
            @Suppress("DEPRECATION")
            device.vibrate(timings, REPEAT_NONE)
        }
    }

    /** Android 12+ uses VibratorManager, older versions the Vibrator service directly. */
    private fun resolveVibrator(context: Context): Vibrator? =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            (context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager)
                ?.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        }

    private companion object {
        const val REPEAT_NONE = -1

        /** timings: wait 0 ms, buzz 18 ms, pause 60 ms, buzz 26 ms, pause 60 ms, buzz 20 ms. */
        val ROLL_TIMINGS = longArrayOf(0L, 18L, 60L, 26L, 60L, 20L)
        val ROLL_AMPLITUDES = intArrayOf(0, 120, 0, 90, 0, 70)

        /** A single soft buzz when the dice settle. */
        val LAND_TIMINGS = longArrayOf(0L, 26L)
        val LAND_AMPLITUDES = intArrayOf(0, 110)
    }
}
