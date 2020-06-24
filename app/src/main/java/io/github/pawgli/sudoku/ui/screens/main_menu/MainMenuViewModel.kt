package io.github.pawgli.sudoku.ui.screens.main_menu

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.github.pawgli.sudoku.utils.GAME_MODE_EASY
import io.github.pawgli.sudoku.utils.GAME_MODE_HARD
import io.github.pawgli.sudoku.utils.GAME_MODE_MEDIUM
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
        _chosenOption.value = GAME_MODE_EASY
    }

    fun onMediumModeClicked() {
        _chosenOption.value = GAME_MODE_MEDIUM
    }

    fun onHardModeClicked() {
        _chosenOption.value = GAME_MODE_HARD
    }

    fun onExitClicked() {
        _chosenOption.value = OPTION_EXIT
    }

    fun onChosenOptionHandled() {
        Timber.d("[onChosenOptionHandled]")
        _chosenOption.value = OPTION_NONE
    }
}