package io.github.pawgli.sudoku.ui.screens.game

import androidx.lifecycle.ViewModel
import timber.log.Timber

class GameViewModel(private val gameMode: String) : ViewModel() {

    var isNotesModeActive: Boolean = false
        private set

    init {
        Timber.d("View model created with $gameMode mode")
    }

    fun onCellSelected(cellIndex: Int) {

    }

    fun onNumberClicked(number: Int) {
        Timber.d("Number $number clicked")
    }

    fun onNotesClicked() {
        Timber.d("Notes clicked")
    }

    fun onUndoClicked() {
        Timber.d("Undo clicked")
    }

    fun onCheckClicked() {
        Timber.d("Check clicked")
    }
}