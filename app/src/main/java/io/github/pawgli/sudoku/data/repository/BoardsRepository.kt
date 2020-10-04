package io.github.pawgli.sudoku.data.repository

import androidx.lifecycle.LiveData
import io.github.pawgli.sudoku.models.Board
import io.github.pawgli.sudoku.utils.Difficulty

interface BoardsRepository {

    suspend fun getEmptyBoard(difficulty: Difficulty)

    suspend fun saveBoard(board: Board): Int

    fun observeBoardResult(): LiveData<Result<Board>>
}