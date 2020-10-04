package io.github.pawgli.sudoku.ui.screens.main_menu

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.github.pawgli.sudoku.utils.Difficulty
import io.github.pawgli.sudoku.utils.Event

class MainMenuViewModel : ViewModel() {

    private var _exitGame = MutableLiveData<Boolean>()
    val exitGame: LiveData<Boolean>
        get() = _exitGame

    private var _startNewGame = MutableLiveData<Event<Difficulty>>()
    val startNewGame: LiveData<Event<Difficulty>>
        get() = _startNewGame

    fun onExitClicked() {
        _exitGame.value = true
    }

    fun onEasyModeClicked() {
        _startNewGame.value = Event(Difficulty.EASY)
    }

    fun onMediumModeClicked() {
        _startNewGame.value = Event(Difficulty.MEDIUM)
    }

    fun onDifficultModeClicked() {
        _startNewGame.value = Event(Difficulty.HARD)
    }
}