package io.github.pawgli.sudoku.ui.screens.main_menu

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import io.github.pawgli.sudoku.getOrAwaitValue
import io.github.pawgli.sudoku.utils.DIFFICULTY_EASY
import io.github.pawgli.sudoku.utils.DIFFICULTY_HARD
import io.github.pawgli.sudoku.utils.DIFFICULTY_MEDIUM
import junit.framework.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class MainMenuViewModelTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    lateinit var mainMenuViewModel: MainMenuViewModel
    @Before
    fun initViewModel() {
        mainMenuViewModel = MainMenuViewModel()
    }

    @Test
    fun onEasyModeClickedTest_setsChosenOptionEvent() {
        mainMenuViewModel.onEasyModeClicked()
        val value = mainMenuViewModel.chosenOption.getOrAwaitValue()
        assertEquals(value.getContentIfNotHandled(), DIFFICULTY_EASY)
    }

    @Test
    fun onMediumModeClickedTest_setsChosenOptionEvent() {
        mainMenuViewModel.onMediumModeClicked()
        val value = mainMenuViewModel.chosenOption.getOrAwaitValue()
        assertEquals(value.getContentIfNotHandled(), DIFFICULTY_MEDIUM)
    }

    @Test
    fun onDifficultModeClickedTest_setsChosenOptionEvent() {
        mainMenuViewModel.onDifficultModeClicked()
        val value = mainMenuViewModel.chosenOption.getOrAwaitValue()
        assertEquals(value.getContentIfNotHandled(), DIFFICULTY_HARD)
    }

    @Test
    fun onExitClickedTest_setsChosenOptionEvent() {
        mainMenuViewModel.onExitClicked()
        val value = mainMenuViewModel.chosenOption.getOrAwaitValue()
        assertEquals(value.getContentIfNotHandled(), OPTION_EXIT)
    }
}