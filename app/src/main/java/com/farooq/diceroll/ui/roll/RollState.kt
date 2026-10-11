package com.farooq.diceroll.ui.roll

import com.farooq.diceroll.domain.model.DiceFaces
import com.farooq.diceroll.domain.model.RollRecord

/**
 * Everything the Roll screen needs to draw itself.
 *
 * It is immutable: the ViewModel publishes a new copy whenever something changes, and Compose
 * simply redraws. The list [faces] always holds exactly one value per dice
 * (one value in single dice mode, two in two dice mode).
 */
data class RollState(
    /** The value currently shown on each dice. Starts at 1, so the app never shows an invalid face. */
    val faces: List<Int> = listOf(DiceFaces.MIN),
    /** `true` while the dice are tumbling. While this is true, new rolls are ignored. */
    val isRolling: Boolean = false,
    /** The last *finished* roll, used for the result text, the total chip and the recent list. */
    val lastRecord: RollRecord? = null,
)
