package com.farooq.diceroll.domain.history

import com.farooq.diceroll.domain.model.DiceFaces
import com.farooq.diceroll.domain.model.DiceMode
import com.farooq.diceroll.domain.model.RollRecord

/**
 * Turns the roll history into text and back again, so it can be stored on the device.
 *
 * Format - one roll per line:
 * ```
 * 1735689600000|one|5
 * 1735689601000|two|4,2
 * ```
 * (`timestamp | mode | faces`)
 *
 * Broken or unknown lines are skipped instead of crashing the app, which means an update of the
 * format can never lose the whole history - see `HistoryCodecTest`.
 */
object HistoryCodec {

    /** How many rolls are kept. Older rolls fall off the end of the list. */
    const val HISTORY_LIMIT: Int = 20

    private const val RECORD_SEPARATOR = "\n"
    private const val FIELD_SEPARATOR = "|"
    private const val FACE_SEPARATOR = ","

    /** Converts the history into the storage format. */
    fun encode(records: List<RollRecord>): String =
        records.joinToString(RECORD_SEPARATOR) { encodeRecord(it) }

    private fun encodeRecord(record: RollRecord): String = listOf(
        record.timestamp.toString(),
        record.mode.storageKey,
        record.faces.joinToString(FACE_SEPARATOR),
    ).joinToString(FIELD_SEPARATOR)

    /** Reads the history back. Unknown or broken entries are ignored. */
    fun decode(raw: String?): List<RollRecord> {
        if (raw.isNullOrBlank()) return emptyList()
        return raw.split(RECORD_SEPARATOR)
            .mapNotNull { decodeRecord(it) }
            .take(HISTORY_LIMIT)
    }

    private fun decodeRecord(line: String): RollRecord? {
        val fields = line.split(FIELD_SEPARATOR)
        if (fields.size != 3) return null

        val timestamp = fields[0].toLongOrNull() ?: return null
        val mode = DiceMode.entries.firstOrNull { it.storageKey == fields[1] } ?: return null
        val faces = fields[2].split(FACE_SEPARATOR).map { it.trim().toIntOrNull() ?: return null }

        if (faces.size != mode.diceCount || !DiceFaces.areValid(faces)) return null
        return RollRecord(timestamp = timestamp, mode = mode, faces = faces)
    }

    /**
     * Adds [record] to the front of the list (newest roll first) and keeps only the
     * [HISTORY_LIMIT] newest rolls.
     */
    fun addNewest(records: List<RollRecord>, record: RollRecord): List<RollRecord> =
        (listOf(record) + records).take(HISTORY_LIMIT)
}
