package io.github.pawgli.sudoku

import android.content.Context
import androidx.annotation.VisibleForTesting
import io.github.pawgli.sudoku.data.local.AppDatabase
import io.github.pawgli.sudoku.data.local.BoardsLocalDataSource
import io.github.pawgli.sudoku.data.local.getDatabase
import io.github.pawgli.sudoku.data.remote.SudokuApi
import io.github.pawgli.sudoku.data.repository.BoardsRepository
import io.github.pawgli.sudoku.data.repository.DefaultBoardsRepository

object ServiceLocator {

    private val lock = Any()

    private var database: AppDatabase? = null
    @Volatile
    var boardsRepository: BoardsRepository? = null
        @VisibleForTesting set

    fun provideBoardsRepository(context: Context): BoardsRepository {
        synchronized(this) {
            return boardsRepository ?: createBoardsRepository(context)
        }
    }

    private fun createBoardsRepository(context: Context): BoardsRepository {
        val newRepository =
            DefaultBoardsRepository(createBoardsLocalDataSource(context), SudokuApi.service)
        boardsRepository = newRepository
        return newRepository
    }

    private fun createBoardsLocalDataSource(context: Context): BoardsLocalDataSource {
        val database = database ?: getDatabase(context)
        return  BoardsLocalDataSource(database.boardsDao)
    }

}