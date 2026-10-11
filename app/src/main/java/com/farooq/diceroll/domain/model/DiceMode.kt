package com.farooq.diceroll.domain.model

/**
 * How many dice the player is rolling.
 *
 * Dice Roll supports two modes:
 *  - [ONE_DICE]: one dice, results 1..6
 *  - [TWO_DICE]: two independent dice, each 1..6, total always 2..12
 */
enum class DiceMode(
    /** How many dice are shown and rolled in this mode. */
    val diceCount: Int,
    /** Key used to save this mode on the device. Never change it once released. */
    val storageKey: String,
) {
    ONE_DICE(diceCount = 1, storageKey = "one"),
    TWO_DICE(diceCount = 2, storageKey = "two"),
    ;

    /** The faces shown before the first roll - always a valid result (e.g. 1 or 1 + 1). */
    val initialFaces: List<Int>
        get() = List(diceCount) { DiceFaces.MIN }

    companion object {
        /** Single dice mode is what the player sees on a fresh install. */
        val DEFAULT: DiceMode = ONE_DICE

        /** Reads a mode that was saved earlier, falling back to [DEFAULT] for unknown values. */
        fun fromStorageKey(key: String?): DiceMode =
            entries.firstOrNull { it.storageKey == key } ?: DEFAULT
    }
}
