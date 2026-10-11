package com.farooq.diceroll.platform

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.SoundPool
import androidx.annotation.RawRes
import com.farooq.diceroll.R
import com.farooq.diceroll.domain.feedback.SoundController

/**
 * Plays the bundled dice sounds (res/raw/dice_roll.wav and dice_land.wav).
 *
 * [SoundPool] is made for short effects: it decodes each clip once, keeps it in memory and can
 * play it again with almost no delay - exactly what a dice roll needs. It runs on the main
 * thread, like every other call in this class, so no locking is required.
 *
 * The sounds are loaded the first time they are needed. If the very first roll happens before
 * loading finished, the sound is played as soon as it is ready.
 */
class SoundPoolController(context: Context) : SoundController {

    private val appContext: Context = context.applicationContext
    private val audioManager: AudioManager? =
        appContext.getSystemService(Context.AUDIO_SERVICE) as? AudioManager

    private val soundPool: SoundPool by lazy { createSoundPool() }

    /** Maps a raw resource (R.raw.*) to the id SoundPool gave it while loading. */
    private val soundIds = mutableMapOf<Int, Int>()

    /** SoundPool ids that finished loading and can be played. */
    private val readySoundIds = mutableSetOf<Int>()

    /** A sound that was requested while it was still loading, played as soon as it is ready. */
    private var pendingSoundId: Int? = null

    override fun playRoll() = play(R.raw.dice_roll)

    override fun playLand() = play(R.raw.dice_land)

    /** Plays a sound effect, unless the phone is on silent. */
    private fun play(@RawRes resId: Int) {
        if (isPhoneSilent) return

        val soundId = loadIfNeeded(resId)
        if (soundId in readySoundIds) {
            soundPool.play(soundId, VOLUME, VOLUME, PRIORITY, /* loop = */ 0, RATE)
        } else {
            pendingSoundId = soundId
        }
    }

    private fun loadIfNeeded(@RawRes resId: Int): Int =
        soundIds.getOrPut(resId) { soundPool.load(appContext, resId, /* priority = */ 1) }

    private fun createSoundPool(): SoundPool = SoundPool.Builder()
        .setMaxStreams(MAX_STREAMS)
        .setAudioAttributes(
            AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()
        )
        .build()
        .apply {
            setOnLoadCompleteListener { _, sampleId, status ->
                if (status == 0) {
                    readySoundIds += sampleId
                    if (pendingSoundId == sampleId) {
                        pendingSoundId = null
                        play(sampleId, VOLUME, VOLUME, PRIORITY, /* loop = */ 0, RATE)
                    }
                }
            }
        }

    /**
     * Respects the phone's sound settings: while the phone is on silent or vibrate only,
     * Dice Roll stays quiet.
     */
    private val isPhoneSilent: Boolean
        get() = audioManager?.ringerMode == AudioManager.RINGER_MODE_SILENT

    private companion object {
        const val MAX_STREAMS = 2
        const val VOLUME = 1.0f
        const val RATE = 1.0f
        const val PRIORITY = 1
    }
}
