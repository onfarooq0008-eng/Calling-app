package com.farooq.diceroll.ui.components

import android.text.format.DateFormat
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.farooq.diceroll.R
import com.farooq.diceroll.domain.model.RollRecord
import java.util.Date

/**
 * One line of the roll history: small dice showing what was rolled, the result text, the time, and
 * the total for two dice rolls.
 */
@Composable
fun RollRow(
    record: RollRecord,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            record.faces.forEach { face ->
                DiceView(
                    face = face,
                    modifier = Modifier.size(34.dp),
                    elevation = 3.dp,
                )
            }
        }

        Spacer(Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = rollRowText(record),
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onBackground,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = rollTimeLabel(record.timestamp),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
            )
        }

        if (record.faces.size > 1) {
            Text(
                text = stringResource(R.string.result_total_chip, record.total),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.secondary,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
            )
        }
    }
}

/** "Rolled a 4" / "4 + 5 = 9" - the short version used in the history lists. */
@Composable
fun rollRowText(record: RollRecord): String = when (record.faces.size) {
    1 -> stringResource(R.string.roll_row_single, record.faces[0])
    else -> stringResource(R.string.roll_row_double, record.faces[0], record.faces[1], record.total)
}

/**
 * The time of a roll, in the format the phone is set to (12 hour or 24 hour clock).
 */
@Composable
fun rollTimeLabel(timestamp: Long): String {
    val context = LocalContext.current
    return remember(timestamp, context) {
        DateFormat.getTimeFormat(context).format(Date(timestamp))
    }
}
