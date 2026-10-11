package com.farooq.diceroll.domain.model

/**
 * One finished roll, for example "two dice: 4 and 5".
 *
 * Records are saved on the device and shown in the roll history.
 * The class refuses to be created with impossible values (like a 7 or the wrong number of
 * dice), so the rest of the app can trust every [RollRecord] it sees.
 */
data class RollRecord(
    /** When the roll happened (milliseconds since 1 January 1970). */
    val timestamp: Long,
    /** The mode that was active while rolling. */
    val mode: DiceMode,
    /** The value of every dice, in display order. */
    val faces: List<Int>,
) {
    init {
        require(faces.size == mode.diceCount) {
            "${mode.name} rolls ${mode.diceCount} dice, but got ${faces.size} faces."
        }
        require(DiceFaces.areValid(faces)) { "Invalid dice values: $faces" }
    }

    /**
     * Sum of all dice.
     * Single dice mode: 1..6. Two dice mode: always between 2 and 12.
     */
    val total: Int
        get() = faces.sum()
}
