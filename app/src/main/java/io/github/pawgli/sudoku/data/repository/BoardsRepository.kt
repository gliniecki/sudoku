package io.github.pawgli.sudoku.data.repository

import androidx.lifecycle.LiveData
import io.github.pawgli.sudoku.models.Board

interface BoardsRepository {

    suspend fun getEmptyBoard(difficulty: String)

    suspend fun saveBoard(board: Board): Int

    fun observeBoardResult(): LiveData<Result<Board>>
}