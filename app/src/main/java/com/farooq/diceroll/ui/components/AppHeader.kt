package com.farooq.diceroll.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.farooq.diceroll.R
import com.farooq.diceroll.ui.theme.AppGradients
import com.farooq.diceroll.ui.theme.ElectricBlue

/**
 * The top bar: the little dice logo, the app name, the tagline "Roll your luck!" and the settings
 * button in the top right corner.
 */
@Composable
fun AppHeader(
    showSettingsButton: Boolean,
    onOpenSettings: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 20.dp, end = 8.dp, top = 8.dp, bottom = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(46.dp)
                .shadow(
                    elevation = 12.dp,
                    shape = RoundedCornerShape(15.dp),
                    clip = false,
                    ambientColor = ElectricBlue.copy(alpha = 0.55f),
                    spotColor = ElectricBlue.copy(alpha = 0.45f),
                )
                .clip(RoundedCornerShape(15.dp))
                .background(AppGradients.brandMark()),
            contentAlignment = Alignment.Center,
        ) {
            Image(
                painter = painterResource(R.drawable.ic_splash_logo),
                contentDescription = stringResource(R.string.cd_app_logo),
                modifier = Modifier.size(31.dp),
            )
        }

        Spacer(Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = stringResource(R.string.app_name),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
            )
            Text(
                text = stringResource(R.string.app_tagline),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
            )
        }

        if (showSettingsButton) {
            IconButton(onClick = onOpenSettings) {
                Icon(
                    painter = painterResource(R.drawable.ic_settings_outlined),
                    contentDescription = stringResource(R.string.cd_open_settings),
                    tint = MaterialTheme.colorScheme.onBackground,
                )
            }
        }
    }
}
