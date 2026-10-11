package com.farooq.diceroll.ui.screens

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.farooq.diceroll.BuildConfig
import com.farooq.diceroll.R
import com.farooq.diceroll.domain.model.AppSettings
import com.farooq.diceroll.domain.model.ThemeMode
import com.farooq.diceroll.ui.components.AppCard
import com.farooq.diceroll.ui.components.AppDivider
import com.farooq.diceroll.ui.components.ClearHistoryDialog
import com.farooq.diceroll.ui.components.SettingActionRow
import com.farooq.diceroll.ui.components.SettingSectionTitle
import com.farooq.diceroll.ui.components.SettingSwitchRow

/**
 * The Settings tab: sound, vibration, theme, clearing the roll history and information about the
 * app. Every switch is saved on the device immediately, so the choice is still there the next time
 * the app is opened.
 */
@Composable
fun SettingsScreen(
    settings: AppSettings,
    rollCount: Int,
    onSoundChange: (Boolean) -> Unit,
    onVibrationChange: (Boolean) -> Unit,
    onThemeChange: (ThemeMode) -> Unit,
    onClearHistory: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var showClearDialog by rememberSaveable { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp),
    ) {
        Spacer(Modifier.height(6.dp))
        Text(
            text = stringResource(R.string.settings_title),
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.Bold,
        )
        Text(
            text = stringResource(R.string.settings_subtitle),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        Spacer(Modifier.height(20.dp))

        // --- Feedback ------------------------------------------------------
        SettingSectionTitle(text = stringResource(R.string.settings_section_feedback))
        AppCard(modifier = Modifier.fillMaxWidth()) {
            SettingSwitchRow(
                iconRes = if (settings.soundEnabled) R.drawable.ic_sound_on else R.drawable.ic_sound_off,
                title = stringResource(R.string.setting_sound),
                description = stringResource(R.string.setting_sound_description),
                checked = settings.soundEnabled,
                onCheckedChange = onSoundChange,
            )
            AppDivider()
            SettingSwitchRow(
                iconRes = R.drawable.ic_vibration,
                title = stringResource(R.string.setting_vibration),
                description = stringResource(R.string.setting_vibration_description),
                checked = settings.vibrationEnabled,
                onCheckedChange = onVibrationChange,
            )
        }

        Spacer(Modifier.height(22.dp))

        // --- Appearance ----------------------------------------------------
        SettingSectionTitle(text = stringResource(R.string.settings_section_appearance))
        AppCard(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = stringResource(R.string.setting_theme),
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = stringResource(R.string.setting_theme_description),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(Modifier.height(14.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                ThemeMode.entries.forEach { mode ->
                    ThemeTile(
                        mode = mode,
                        selected = mode == settings.themeMode,
                        onClick = { onThemeChange(mode) },
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }

        Spacer(Modifier.height(22.dp))

        // --- Data ----------------------------------------------------------
        SettingSectionTitle(text = stringResource(R.string.settings_section_data))
        AppCard(modifier = Modifier.fillMaxWidth()) {
            SettingActionRow(
                iconRes = R.drawable.ic_delete,
                title = stringResource(R.string.clear_history),
                description = if (rollCount == 0) {
                    stringResource(R.string.setting_clear_history_description_none)
                } else {
                    pluralStringResource(
                        id = R.plurals.setting_clear_history_description,
                        count = rollCount,
                        rollCount,
                    )
                },
                onClick = { showClearDialog = true },
                tint = MaterialTheme.colorScheme.error,
            )
        }

        Spacer(Modifier.height(22.dp))

        // --- About ---------------------------------------------------------
        SettingSectionTitle(text = stringResource(R.string.settings_section_about))
        AppCard(modifier = Modifier.fillMaxWidth()) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    painter = painterResource(R.drawable.ic_info),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp),
                )
                Spacer(Modifier.width(10.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(R.string.about_version, BuildConfig.VERSION_NAME),
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onBackground,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        text = stringResource(R.string.about_made_with),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
            Spacer(Modifier.height(14.dp))
            AboutBullet(text = stringResource(R.string.about_offline))
            Spacer(Modifier.height(8.dp))
            AboutBullet(text = stringResource(R.string.about_privacy))
        }

        Spacer(Modifier.height(30.dp))
    }

    if (showClearDialog) {
        ClearHistoryDialog(
            rollCount = rollCount,
            onConfirm = {
                showClearDialog = false
                onClearHistory()
            },
            onDismiss = { showClearDialog = false },
        )
    }
}

@Composable
private fun AboutBullet(text: String, modifier: Modifier = Modifier) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        Icon(
            painter = painterResource(R.drawable.ic_check),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.size(16.dp),
        )
        Spacer(Modifier.width(10.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun ThemeTile(
    mode: ThemeMode,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val accent = MaterialTheme.colorScheme.primary
    val shape = MaterialTheme.shapes.medium
    val iconTint = if (selected) accent else MaterialTheme.colorScheme.onSurfaceVariant

    Column(
        modifier = modifier
            .clip(shape)
            .background(
                color = if (selected) {
                    accent.copy(alpha = 0.14f)
                } else {
                    MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
                },
                shape = shape,
            )
            .border(
                width = if (selected) 2.dp else 1.dp,
                color = if (selected) accent else MaterialTheme.colorScheme.outline.copy(alpha = 0.45f),
                shape = shape,
            )
            .clickable(role = Role.RadioButton, onClick = onClick)
            .padding(vertical = 12.dp, horizontal = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(
            painter = painterResource(mode.iconRes()),
            contentDescription = null,
            tint = iconTint,
            modifier = Modifier.size(22.dp),
        )
        Spacer(Modifier.height(8.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (selected) {
                Icon(
                    painter = painterResource(R.drawable.ic_check),
                    contentDescription = null,
                    tint = accent,
                    modifier = Modifier.size(13.dp),
                )
                Spacer(Modifier.width(3.dp))
            }
            Text(
                text = stringResource(mode.labelRes()),
                style = MaterialTheme.typography.labelMedium,
                color = if (selected) accent else MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@DrawableRes
private fun ThemeMode.iconRes(): Int = when (this) {
    ThemeMode.LIGHT -> R.drawable.ic_theme_light
    ThemeMode.DARK -> R.drawable.ic_theme_dark
    ThemeMode.SYSTEM -> R.drawable.ic_theme_system
}

@StringRes
private fun ThemeMode.labelRes(): Int = when (this) {
    ThemeMode.LIGHT -> R.string.theme_light
    ThemeMode.DARK -> R.string.theme_dark
    ThemeMode.SYSTEM -> R.string.theme_system
}
