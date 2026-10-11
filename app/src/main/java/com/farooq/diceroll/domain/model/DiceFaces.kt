package com.farooq.diceroll.domain.model

/**
 * Everything about the dice itself: which numbers are allowed and where the dots ("pips")
 * sit on each face.
 *
 * This is plain Kotlin with no Android or Compose code in it, which makes it very easy to
 * unit test. The UI layer turns [pipsFor] into actual drawings (see ui/components/DiceView.kt).
 */
object DiceFaces {

    /** Lowest possible value of a dice. */
    const val MIN: Int = 1

    /** Highest possible value of a dice. */
    const val MAX: Int = 6

    /** How many different faces a dice has. */
    const val COUNT: Int = MAX - MIN + 1

    /** All valid faces, 1..6. Handy for tests and statistics. */
    val ALL: List<Int> = (MIN..MAX).toList()

    /** A single dot on a dice face, in unit coordinates.
     *  `x = 0f` is the left edge of the dice, `x = 1f` the right edge (same for `y`). */
    data class Pip(val x: Float, val y: Float)

    private const val LEFT = 0.26f
    private const val MIDDLE = 0.5f
    private const val RIGHT = 0.74f
    private const val TOP = 0.26f
    private const val BOTTOM = 0.74f

    /**
     * The classic dice pip layout:
     *
     * 1: centre - 2: diagonal - 3: diagonal + centre - 4: four corners -
     * 5: four corners + centre - 6: two columns of three
     */
    val PIP_LAYOUT: Map<Int, List<Pip>> = mapOf(
        1 to listOf(Pip(MIDDLE, MIDDLE)),
        2 to listOf(Pip(LEFT, TOP), Pip(RIGHT, BOTTOM)),
        3 to listOf(Pip(LEFT, TOP), Pip(MIDDLE, MIDDLE), Pip(RIGHT, BOTTOM)),
        4 to listOf(Pip(LEFT, TOP), Pip(RIGHT, TOP), Pip(LEFT, BOTTOM), Pip(RIGHT, BOTTOM)),
        5 to listOf(
            Pip(LEFT, TOP),
            Pip(RIGHT, TOP),
            Pip(MIDDLE, MIDDLE),
            Pip(LEFT, BOTTOM),
            Pip(RIGHT, BOTTOM),
        ),
        6 to listOf(
            Pip(LEFT, TOP),
            Pip(RIGHT, TOP),
            Pip(LEFT, MIDDLE),
            Pip(RIGHT, MIDDLE),
            Pip(LEFT, BOTTOM),
            Pip(RIGHT, BOTTOM),
        ),
    )

    /** `true` when [face] is a real dice value (1..6). */
    fun isValid(face: Int): Boolean = face in MIN..MAX

    /** Returns [face] if it is valid, otherwise throws - useful to catch programmer mistakes early. */
    fun requireValid(face: Int): Int {
        require(isValid(face)) { "A dice face must be between $MIN and $MAX, but was $face." }
        return face
    }

    /** `true` when [faces] is a non-empty list of valid faces. */
    fun areValid(faces: List<Int>): Boolean = faces.isNotEmpty() && faces.all(::isValid)

    /** The pip positions of a face. Throws for values outside 1..6. */
    fun pipsFor(face: Int): List<Pip> = PIP_LAYOUT.getValue(requireValid(face))

    /** How many dots a face shows - exactly the same number as the face value. */
    fun pipCount(face: Int): Int = pipsFor(face).size
}
