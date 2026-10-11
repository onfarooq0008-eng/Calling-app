package com.farooq.diceroll.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.farooq.diceroll.R
import com.farooq.diceroll.domain.model.RollRecord

/**
 * The "Recent rolls" section on the Roll screen: the last few results, or a friendly hint when the
 * player has not rolled yet.
 */
@Composable
fun RecentRollsCard(
    records: List<RollRecord>,
    onViewAll: () -> Unit,
    modifier: Modifier = Modifier,
) {
    AppCard(modifier = modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                painter = painterResource(R.drawable.ic_history_outlined),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(18.dp),
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text = stringResource(R.string.recent_rolls),
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f),
            )
            if (records.isNotEmpty()) {
                TextButton(onClick = onViewAll) {
                    Text(
                        text = stringResource(R.string.recent_rolls_view_all),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.secondary,
                    )
                }
            }
        }

        Spacer(Modifier.height(4.dp))

        val visibleRecords = records.take(RECENT_ROLLS)
        if (visibleRecords.isEmpty()) {
            EmptyRollsHint()
        } else {
            Column(modifier = Modifier.fillMaxWidth()) {
                visibleRecords.forEachIndexed { index, record ->
                    RollRow(record = record)
                    if (index != visibleRecords.lastIndex) {
                        AppDivider()
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyRollsHint() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp, bottom = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                    shape = MaterialTheme.shapes.medium,
                ),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_dice_outlined),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(22.dp),
            )
        }
        Spacer(Modifier.width(12.dp))
        Text(
            text = stringResource(R.string.recent_rolls_empty),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

/** How many rolls the "Recent rolls" card shows. */
private const val RECENT_ROLLS = 4
