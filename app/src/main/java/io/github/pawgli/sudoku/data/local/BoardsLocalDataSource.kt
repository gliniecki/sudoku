package io.github.pawgli.sudoku.data.local

import io.github.pawgli.sudoku.data.repository.Result
import io.github.pawgli.sudoku.models.Board
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import io.github.pawgli.sudoku.data.repository.Result.Success
import io.github.pawgli.sudoku.data.repository.Result.Error

class BoardsLocalDataSource (
    private val boardsDao: BoardsDao,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO) {

    suspend fun getRandomEmptyBoard(difficulty: String): Result<Board> = withContext(ioDispatcher) {
            return@withContext try {
                val boards = boardsDao.getAllEmptyBoards(difficulty)
                val randomDatabaseBoard = boards.random()
                Success(randomDatabaseBoard.asDomainModel())
            } catch (e: Exception) {
                Error(e)
            }
    }

    suspend fun getBoardSavedByUser(boardId: Int): Result<Board> = withContext(ioDispatcher) {
        return@withContext try {
            val databaseBoard = boardsDao.getBoardById(boardId)
            Success(databaseBoard.asDomainModel())
        } catch (e: Exception) {
            Error(e)
        }
    }

    suspend fun saveBoard(
        board: Board,
        isSavedByUser: Boolean = false) = withContext(ioDispatcher) {
            val databaseBoard =  board.toDatabaseModel(isSavedByUser)
            boardsDao.insertBoard(databaseBoard)
    }
}

fun Board.toDatabaseModel(isSavedByUser: Boolean): DatabaseBoard {
    return DatabaseBoard(
        difficulty = this.difficulty,
        size = this.size,
        cells = this.getCells(),
        isSavedByUser = isSavedByUser)
}