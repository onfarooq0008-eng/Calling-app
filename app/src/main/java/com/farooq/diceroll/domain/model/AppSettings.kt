package com.farooq.diceroll.domain.model

/**
 * Everything the player can change in the Settings screen.
 * The values are stored on the device with DataStore and loaded again when the app starts.
 */
data class AppSettings(
    /** Play the dice rattle sound while rolling. */
    val soundEnabled: Boolean = true,
    /** Buzz shortly when the dice land. */
    val vibrationEnabled: Boolean = true,
    /** Light, dark, or follow the phone setting. */
    val themeMode: ThemeMode = ThemeMode.DEFAULT,
    /** One dice or two dice. */
    val diceMode: DiceMode = DiceMode.DEFAULT,
)

/** The three theme choices offered in the Settings screen. */
enum class ThemeMode(
    /** Key used to save the setting on the device. Never change it once released. */
    val storageKey: String,
) {
    LIGHT("light"),
    DARK("dark"),
    SYSTEM("system"),
    ;

    companion object {
        /** Dice Roll is designed as a dark, premium looking game, so dark is the default. */
        val DEFAULT: ThemeMode = DARK

        /** Reads a theme that was saved earlier, falling back to [DEFAULT] for unknown values. */
        fun fromStorageKey(key: String?): ThemeMode =
            entries.firstOrNull { it.storageKey == key } ?: DEFAULT
    }
}
