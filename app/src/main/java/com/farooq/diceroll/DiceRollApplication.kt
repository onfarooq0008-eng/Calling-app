package com.farooq.diceroll

import android.app.Application
import com.farooq.diceroll.di.AppContainer

/**
 * The app itself. Android creates exactly one [Application] per process, which makes it the
 * right place for objects that should live as long as the app does.
 */
class DiceRollApplication : Application() {

    /** Settings, roll history, dice roller, sound and vibration. */
    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }
}
