package io.github.pawgli.sudoku.ui.screens.game

import androidx.lifecycle.ViewModel
import timber.log.Timber

class GameViewModel(private val gameMode: String) : ViewModel() {

    init {
        Timber.d("View model created with $gameMode mode")
    }
}