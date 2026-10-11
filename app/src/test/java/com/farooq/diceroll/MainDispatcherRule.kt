package com.farooq.diceroll

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.rules.TestWatcher
import org.junit.runner.Description

/**
 * The ViewModel runs its work on [Dispatchers.Main] (Android's main thread), which does not exist
 * in a JVM unit test. This rule replaces it with a test dispatcher that runs the app's coroutines
 * step by step, so a test can decide *when* the rolling animation continues.
 */
class MainDispatcherRule(
    private val dispatcher: CoroutineDispatcher = StandardTestDispatcher(),
) : TestWatcher() {

    override fun starting(description: Description) {
        Dispatchers.setMain(dispatcher)
    }

    override fun finished(description: Description) {
        Dispatchers.resetMain()
    }
}
