package com.farooq.diceroll.ui

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.farooq.diceroll.R
import com.farooq.diceroll.ui.components.AppBackground
import com.farooq.diceroll.ui.components.AppHeader
import com.farooq.diceroll.ui.roll.RollViewModel
import com.farooq.diceroll.ui.screens.HistoryScreen
import com.farooq.diceroll.ui.screens.RollScreen
import com.farooq.diceroll.ui.screens.SettingsScreen
import com.farooq.diceroll.ui.theme.DiceRollTheme

/**
 * The whole app: the theme, the background, the top bar, the three tabs of the bottom navigation
 * and everything in between.
 *
 * Everything you see is built from the ViewModel's state, so the screens hold no logic of their own.
 * Tap "Roll" to roll the dice, "History" to look back at your last rolls and "Settings" to change
 * sound, vibration and theme.
 */
@Composable
fun DiceRollApp(
    viewModel: RollViewModel,
    modifier: Modifier = Modifier,
) {
    val settings by viewModel.settings.collectAsStateWithLifecycle()
    val rollState by viewModel.rollState.collectAsStateWithLifecycle()
    val history by viewModel.history.collectAsStateWithLifecycle()

    DiceRollTheme(themeMode = settings.themeMode) {
        var selectedTab by rememberSaveable { mutableStateOf(AppTab.ROLL) }

        AppBackground(modifier = modifier) {
            Scaffold(
                // The gradient behind the app is what the user should see, so the Scaffold and the
                // navigation bar are transparent.
                containerColor = Color.Transparent,
                bottomBar = {
                    DiceRollBottomBar(
                        selectedTab = selectedTab,
                        onTabSelected = { tab -> selectedTab = tab },
                    )
                },
            ) { innerPadding ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                ) {
                    AppHeader(
                        showSettingsButton = selectedTab != AppTab.SETTINGS,
                        onOpenSettings = { selectedTab = AppTab.SETTINGS },
                    )

                    Crossfade(
                        targetState = selectedTab,
                        animationSpec = tween(durationMillis = 220),
                        label = "tabs",
                    ) { tab ->
                        when (tab) {
                            AppTab.ROLL -> RollScreen(
                                state = rollState,
                                mode = settings.diceMode,
                                history = history,
                                onRoll = viewModel::roll,
                                onModeSelected = viewModel::setMode,
                                onViewHistory = { selectedTab = AppTab.HISTORY },
                            )

                            AppTab.HISTORY -> HistoryScreen(
                                history = history,
                                onClearHistory = viewModel::clearHistory,
                            )

                            AppTab.SETTINGS -> SettingsScreen(
                                settings = settings,
                                rollCount = history.size,
                                onSoundChange = viewModel::setSoundEnabled,
                                onVibrationChange = viewModel::setVibrationEnabled,
                                onThemeChange = viewModel::setThemeMode,
                                onClearHistory = viewModel::clearHistory,
                            )
                        }
                    }
                }
            }
        }
    }
}

/** The bottom navigation bar: Roll, History and Settings. */
@Composable
private fun DiceRollBottomBar(
    selectedTab: AppTab,
    onTabSelected: (AppTab) -> Unit,
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.97f),
        shape = RoundedCornerShape(topStart = 26.dp, topEnd = 26.dp),
        tonalElevation = 0.dp,
        shadowElevation = 14.dp,
    ) {
        NavigationBar(
            containerColor = Color.Transparent,
            tonalElevation = 0.dp,
        ) {
            AppTab.entries.forEach { tab ->
                val selected = tab == selectedTab
                NavigationBarItem(
                    selected = selected,
                    onClick = { onTabSelected(tab) },
                    icon = {
                        Icon(
                            painter = painterResource(if (selected) tab.selectedIconRes else tab.iconRes),
                            contentDescription = null,
                            modifier = Modifier.size(23.dp),
                        )
                    },
                    label = {
                        Text(
                            text = stringResource(tab.labelRes),
                            style = MaterialTheme.typography.labelSmall,
                        )
                    },
                    alwaysShowLabel = true,
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        selectedTextColor = MaterialTheme.colorScheme.primary,
                        indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.16f),
                        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    ),
                )
            }
        }
    }
}

/** The three tabs of the app. */
private enum class AppTab(
    @StringRes val labelRes: Int,
    @DrawableRes val iconRes: Int,
    @DrawableRes val selectedIconRes: Int,
) {
    ROLL(
        labelRes = R.string.nav_roll,
        iconRes = R.drawable.ic_dice_outlined,
        selectedIconRes = R.drawable.ic_dice_filled,
    ),
    HISTORY(
        labelRes = R.string.nav_history,
        iconRes = R.drawable.ic_history_outlined,
        selectedIconRes = R.drawable.ic_history_filled,
    ),
    SETTINGS(
        labelRes = R.string.nav_settings,
        iconRes = R.drawable.ic_settings_outlined,
        selectedIconRes = R.drawable.ic_settings_filled,
    ),
}
