package com.farooq.diceroll

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.viewmodel.compose.viewModel
import com.farooq.diceroll.ui.DiceRollApp
import com.farooq.diceroll.ui.roll.RollViewModel

/**
 * The app's only screen. It shows the splash screen while Android starts the process, then hands
 * over to Compose.
 *
 * The [RollViewModel] is created here and lives as long as the screen: turning the phone does not
 * interrupt a roll and does not lose the result.
 */
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)

        // Draw behind the status and navigation bars, like modern Android apps do.
        enableEdgeToEdge()

        val container = (application as DiceRollApplication).container

        setContent {
            val viewModel: RollViewModel = viewModel(factory = RollViewModel.factory(container))
            DiceRollApp(viewModel = viewModel)
        }
    }
}
