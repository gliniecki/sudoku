package io.github.pawgli.sudoku.ui.screens.game

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import io.github.pawgli.sudoku.data.repository.BoardsRepository
import io.github.pawgli.sudoku.utils.Difficulty
import java.lang.IllegalArgumentException

class GameViewModelFactory(
    private val difficulty: Difficulty,
    private val boardsRepository: BoardsRepository) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GameViewModel::class.java)) {
            return GameViewModel(difficulty, boardsRepository) as T
        }
        throw IllegalArgumentException("Invalid ViewModel class")
    }
}