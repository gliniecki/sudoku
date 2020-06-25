package io.github.pawgli.sudoku.ui.screens.main_menu

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.github.pawgli.sudoku.utils.DIFFICULTY_EASY
import io.github.pawgli.sudoku.utils.DIFFICULTY_HARD
import io.github.pawgli.sudoku.utils.DIFFICULTY_MEDIUM
import timber.log.Timber

const val OPTION_NONE = "none"
const val OPTION_EXIT = "exit"

class MainMenuViewModel : ViewModel() {

    private var _chosenOption = MutableLiveData<String>()
    val chosenOption: LiveData<String>
        get() = _chosenOption

    init {
        _chosenOption.value = OPTION_NONE
    }

    fun onEasyModeClicked() {
        _chosenOption.value = DIFFICULTY_EASY
    }

    fun onMediumModeClicked() {
        _chosenOption.value = DIFFICULTY_MEDIUM
    }

    fun onHardModeClicked() {
        _chosenOption.value = DIFFICULTY_HARD
    }

    fun onExitClicked() {
        _chosenOption.value = OPTION_EXIT
    }

    fun onChosenOptionHandled() {
        _chosenOption.value = OPTION_NONE
    }
}