package io.github.pawgli.sudoku.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.github.pawgli.sudoku.data.local.BoardsLocalDataSource
import io.github.pawgli.sudoku.models.Board
import io.github.pawgli.sudoku.data.remote.SudokuApiService
import io.github.pawgli.sudoku.data.remote.asDomainModel
import io.github.pawgli.sudoku.utils.Difficulty
import timber.log.Timber

class DefaultBoardsRepository (
    private val boardsLocalDataSource: BoardsLocalDataSource,
    private val boardsRemoteDataSource: SudokuApiService) : BoardsRepository {

    private val boardResult = MutableLiveData<Result<Board>>()

    override suspend fun getEmptyBoard(difficulty: Difficulty) {
        boardResult.value = Result.Loading
        try {
            val networkBoard = boardsRemoteDataSource.getBoard(difficulty.value)
            val board = networkBoard.asDomainModel(difficulty)
            onBoardFetched(board)
        } catch (t: Throwable) {
            Timber.d("Problem witch fetching a board: $t")
            boardResult.value = boardsLocalDataSource.getRandomEmptyBoard(difficulty)
        }
    }

    private suspend fun onBoardFetched(board: Board) {
        boardResult.value = Result.Success(board)
        boardsLocalDataSource.saveBoard(board, isSavedByUser = false)
    }

    override suspend fun saveBoard(board: Board): Int {
        TODO("Not yet implemented")
    }

    override fun observeBoardResult(): LiveData<Result<Board>> = boardResult
}


