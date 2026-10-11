package com.farooq.diceroll.ui.roll

import com.farooq.diceroll.MainDispatcherRule
import com.farooq.diceroll.domain.history.HistoryCodec
import com.farooq.diceroll.domain.model.AppSettings
import com.farooq.diceroll.domain.model.DiceFaces
import com.farooq.diceroll.domain.model.DiceMode
import com.farooq.diceroll.domain.model.ThemeMode
import com.farooq.diceroll.domain.roll.DiceRoller
import com.farooq.diceroll.domain.roll.RollTiming
import com.farooq.diceroll.testutil.FakeHistoryStore
import com.farooq.diceroll.testutil.FakeSettingsStore
import com.farooq.diceroll.testutil.RecordingHapticController
import com.farooq.diceroll.testutil.RecordingSoundController
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import kotlin.random.Random

/**
 * The rules of the game, tested without a phone:
 * results are always 1..6, rapid taps cannot roll twice, the history is saved, and the settings
 * are remembered.
 */
class RollViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val sound = RecordingSoundController()
    private val haptics = RecordingHapticController()
    private var now = 1_000_000L

    private fun createViewModel(
        settingsStore: FakeSettingsStore = FakeSettingsStore(),
        historyStore: FakeHistoryStore = FakeHistoryStore(),
        seed: Int = 42,
    ) = RollViewModel(
        settingsStore = settingsStore,
        historyStore = historyStore,
        diceRoller = DiceRoller(Random(seed)),
        soundController = sound,
        hapticController = haptics,
        clock = { now++ },
    )

    // --- starting up -------------------------------------------------------

    @Test
    fun `the app starts with a valid dice showing`() = runTest {
        val viewModel = createViewModel()
        advanceUntilIdle()

        assertEquals(listOf(1), viewModel.rollState.value.faces)
        assertFalse(viewModel.rollState.value.isRolling)
        assertNull(viewModel.rollState.value.lastRecord)
        assertTrue(DiceFaces.areValid(viewModel.rollState.value.faces))
    }

    @Test
    fun `the saved settings are loaded again when the app restarts`() = runTest {
        val stored = AppSettings(
            soundEnabled = false,
            vibrationEnabled = false,
            themeMode = ThemeMode.LIGHT,
            diceMode = DiceMode.TWO_DICE,
        )
        val viewModel = createViewModel(settingsStore = FakeSettingsStore(stored))
        advanceUntilIdle()

        assertEquals(stored, viewModel.settings.value)
        assertEquals("two dice mode must show two dice", 2, viewModel.rollState.value.faces.size)
    }

    // --- rolling -----------------------------------------------------------

    @Test
    fun `every roll is between 1 and 6`() = runTest {
        val viewModel = createViewModel()
        advanceUntilIdle()

        repeat(60) {
            viewModel.roll()
            advanceUntilIdle()

            val record = viewModel.rollState.value.lastRecord
            assertNotNull(record)
            assertEquals(1, record!!.faces.size)
            assertTrue("rolled ${record.faces}", DiceFaces.areValid(record.faces))
            assertTrue(record.total in 1..6)
        }
    }

    @Test
    fun `the result on screen is the result that was saved`() = runTest {
        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.roll()
        advanceUntilIdle()

        val record = viewModel.rollState.value.lastRecord!!
        assertFalse(viewModel.rollState.value.isRolling)
        assertEquals(record.faces, viewModel.rollState.value.faces)
        assertEquals(record, viewModel.history.value.first())
    }

    @Test
    fun `the dice face changes while the animation runs`() = runTest {
        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.roll()
        runCurrent()
        val seenFaces = mutableSetOf(viewModel.rollState.value.faces)
        repeat(6) {
            advanceTimeBy(RollTiming.FRAME_INTERVAL_MS + 1)
            seenFaces += viewModel.rollState.value.faces
        }

        assertTrue("the dice should tumble, not stay frozen", seenFaces.size > 1)
        assertTrue("the roll is still running halfway through", viewModel.rollState.value.isRolling)

        advanceUntilIdle() // let the roll finish
        assertFalse(viewModel.rollState.value.isRolling)
    }

    @Test
    fun `two very fast taps only roll once`() = runTest {
        val historyStore = FakeHistoryStore()
        val viewModel = createViewModel(historyStore = historyStore)
        advanceUntilIdle()

        viewModel.roll()
        viewModel.roll()
        viewModel.roll()
        advanceUntilIdle()

        assertEquals(1, historyStore.added.size)
        assertEquals(1, sound.rollSounds)
    }

    @Test
    fun `tapping again during the animation is ignored`() = runTest {
        val historyStore = FakeHistoryStore()
        val viewModel = createViewModel(historyStore = historyStore)
        advanceUntilIdle()

        viewModel.roll()
        runCurrent() // the animation has started
        assertTrue(viewModel.rollState.value.isRolling)

        viewModel.roll() // ignored
        advanceUntilIdle()

        assertEquals(1, historyStore.added.size)
        assertFalse(viewModel.rollState.value.isRolling)
    }

    @Test
    fun `a roll can be started again after the animation finished`() = runTest {
        val historyStore = FakeHistoryStore()
        val viewModel = createViewModel(historyStore = historyStore)
        advanceUntilIdle()

        viewModel.roll()
        advanceUntilIdle()
        viewModel.roll()
        advanceUntilIdle()

        assertEquals(2, historyStore.added.size)
    }

    // --- two dice mode -----------------------------------------------------

    @Test
    fun `two dice mode rolls two dice and the total is always 2 to 12`() = runTest {
        val viewModel = createViewModel(settingsStore = FakeSettingsStore(AppSettings(diceMode = DiceMode.TWO_DICE)))
        advanceUntilIdle()

        repeat(60) {
            viewModel.roll()
            advanceUntilIdle()

            val record = viewModel.rollState.value.lastRecord!!
            assertEquals(2, record.faces.size)
            assertTrue(DiceFaces.areValid(record.faces))
            assertTrue("total was ${record.total}", record.total in 2..12)
            assertEquals(record.faces.sum(), record.total)
        }
    }

    @Test
    fun `switching to two dice shows two dice and saves the choice`() = runTest {
        val settingsStore = FakeSettingsStore()
        val viewModel = createViewModel(settingsStore = settingsStore)
        advanceUntilIdle()

        viewModel.setMode(DiceMode.TWO_DICE)
        advanceUntilIdle()

        assertEquals(DiceMode.TWO_DICE, viewModel.settings.value.diceMode)
        assertEquals(listOf(DiceMode.TWO_DICE), settingsStore.savedModes)
        assertEquals(2, viewModel.rollState.value.faces.size)
        assertEquals(listOf(1, 1), viewModel.rollState.value.faces)
    }

    @Test
    fun `switching back to one dice works as well`() = runTest {
        val viewModel = createViewModel(settingsStore = FakeSettingsStore(AppSettings(diceMode = DiceMode.TWO_DICE)))
        advanceUntilIdle()
        assertEquals(2, viewModel.rollState.value.faces.size)

        viewModel.setMode(DiceMode.ONE_DICE)
        advanceUntilIdle()

        assertEquals(1, viewModel.rollState.value.faces.size)
        assertEquals(listOf(1), viewModel.rollState.value.faces)
        assertEquals(DiceMode.ONE_DICE, viewModel.settings.value.diceMode)
    }

    @Test
    fun `switching dice mode stops a running roll without saving it`() = runTest {
        val historyStore = FakeHistoryStore()
        val viewModel = createViewModel(historyStore = historyStore)
        advanceUntilIdle()

        viewModel.roll()
        runCurrent()
        assertTrue(viewModel.rollState.value.isRolling)

        viewModel.setMode(DiceMode.TWO_DICE)
        advanceUntilIdle()

        assertFalse(viewModel.rollState.value.isRolling)
        assertEquals(2, viewModel.rollState.value.faces.size)
        assertTrue("a cancelled roll must not be saved", historyStore.added.isEmpty())
    }

    @Test
    fun `other settings are not touched when the dice mode changes`() = runTest {
        val settingsStore = FakeSettingsStore(
            AppSettings(soundEnabled = false, vibrationEnabled = false, themeMode = ThemeMode.LIGHT),
        )
        val viewModel = createViewModel(settingsStore = settingsStore)
        advanceUntilIdle()

        viewModel.setMode(DiceMode.TWO_DICE)
        advanceUntilIdle()

        val settings = viewModel.settings.value
        assertFalse(settings.soundEnabled)
        assertFalse(settings.vibrationEnabled)
        assertEquals(ThemeMode.LIGHT, settings.themeMode)
    }

    // --- sound and vibration ----------------------------------------------

    @Test
    fun `sound and vibration play once per roll`() = runTest {
        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.roll()
        advanceUntilIdle()

        assertEquals(1, sound.rollSounds)
        assertEquals(1, sound.landSounds)
        assertEquals(1, haptics.rollFeedbacks)
        assertEquals(1, haptics.landFeedbacks)
    }

    @Test
    fun `switched off sound and vibration stay silent`() = runTest {
        val settingsStore = FakeSettingsStore()
        val viewModel = createViewModel(settingsStore = settingsStore)
        advanceUntilIdle()

        viewModel.setSoundEnabled(false)
        viewModel.setVibrationEnabled(false)
        advanceUntilIdle()

        viewModel.roll()
        advanceUntilIdle()

        assertEquals(listOf(false), settingsStore.savedSound)
        assertEquals(listOf(false), settingsStore.savedVibration)
        assertEquals(0, sound.rollSounds)
        assertEquals(0, sound.landSounds)
        assertEquals(0, haptics.rollFeedbacks)
        assertEquals(0, haptics.landFeedbacks)
    }

    @Test
    fun `sound and vibration can be switched on again`() = runTest {
        val settingsStore = FakeSettingsStore(AppSettings(soundEnabled = false, vibrationEnabled = false))
        val viewModel = createViewModel(settingsStore = settingsStore)
        advanceUntilIdle()

        viewModel.setSoundEnabled(true)
        viewModel.setVibrationEnabled(true)
        advanceUntilIdle()
        viewModel.roll()
        advanceUntilIdle()

        assertTrue(viewModel.settings.value.soundEnabled)
        assertEquals(1, sound.rollSounds)
        assertEquals(1, haptics.landFeedbacks)
    }

    // --- history -----------------------------------------------------------

    @Test
    fun `rolls are saved to the history and never exceed the limit`() = runTest {
        val historyStore = FakeHistoryStore()
        val viewModel = createViewModel(historyStore = historyStore)
        advanceUntilIdle()

        repeat(HistoryCodec.HISTORY_LIMIT + 5) {
            viewModel.roll()
            advanceUntilIdle()
        }

        assertEquals(HistoryCodec.HISTORY_LIMIT, viewModel.history.value.size)
        assertEquals(HistoryCodec.HISTORY_LIMIT + 5, historyStore.added.size)
        // newest first
        assertEquals(historyStore.added.last(), viewModel.history.value.first())
    }

    @Test
    fun `clearing the history empties the list but keeps the last result on screen`() = runTest {
        val historyStore = FakeHistoryStore()
        val viewModel = createViewModel(historyStore = historyStore)
        advanceUntilIdle()

        viewModel.roll()
        advanceUntilIdle()
        val lastResult = viewModel.rollState.value.lastRecord

        viewModel.clearHistory()
        advanceUntilIdle()

        assertTrue(viewModel.history.value.isEmpty())
        assertEquals(1, historyStore.clearCount)
        assertEquals(lastResult, viewModel.rollState.value.lastRecord)
        assertEquals(lastResult!!.faces, viewModel.rollState.value.faces)
    }

    // --- appearance --------------------------------------------------------

    @Test
    fun `the theme choice is saved`() = runTest {
        val settingsStore = FakeSettingsStore()
        val viewModel = createViewModel(settingsStore = settingsStore)
        advanceUntilIdle()

        viewModel.setThemeMode(ThemeMode.LIGHT)
        advanceUntilIdle()

        assertEquals(ThemeMode.LIGHT, viewModel.settings.value.themeMode)
        assertEquals(listOf(ThemeMode.LIGHT), settingsStore.savedThemes)
    }

    @Test
    fun `every saved roll keeps the mode and time it was rolled with`() = runTest {
        val historyStore = FakeHistoryStore()
        val viewModel = createViewModel(historyStore = historyStore)
        advanceUntilIdle()

        viewModel.roll()
        advanceUntilIdle()
        viewModel.setMode(DiceMode.TWO_DICE)
        advanceUntilIdle()
        viewModel.roll()
        advanceUntilIdle()

        assertEquals(DiceMode.ONE_DICE, historyStore.added[0].mode)
        assertEquals(DiceMode.TWO_DICE, historyStore.added[1].mode)
        assertTrue(historyStore.added[1].timestamp > historyStore.added[0].timestamp)
    }
}
