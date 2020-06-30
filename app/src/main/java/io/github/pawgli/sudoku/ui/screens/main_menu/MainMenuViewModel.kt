package io.github.pawgli.sudoku.ui.screens.main_menu

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.github.pawgli.sudoku.utils.DIFFICULTY_EASY
import io.github.pawgli.sudoku.utils.DIFFICULTY_HARD
import io.github.pawgli.sudoku.utils.DIFFICULTY_MEDIUM
import io.github.pawgli.sudoku.utils.Event

const val OPTION_EXIT = "exit"

class MainMenuViewModel : ViewModel() {

    private var _chosenOption = MutableLiveData<Event<String>>()
    val chosenOption: LiveData<Event<String>>
        get() = _chosenOption

    fun onEasyModeClicked() {
        _chosenOption.value = Event(DIFFICULTY_EASY)
    }

    fun onMediumModeClicked() {
        _chosenOption.value = Event(DIFFICULTY_MEDIUM)
    }

    fun onDifficultModeClicked() {
        _chosenOption.value = Event(DIFFICULTY_HARD)
    }

    fun onExitClicked() {
        _chosenOption.value = Event(OPTION_EXIT)
    }
}