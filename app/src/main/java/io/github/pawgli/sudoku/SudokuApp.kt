package io.github.pawgli.sudoku

import android.app.Application
import android.content.Context
import io.github.pawgli.sudoku.data.repository.BoardsRepository
import timber.log.Timber

class SudokuApp : Application() {

    init {
        instance = this
    }

    val boardsRepository: BoardsRepository
        get() = ServiceLocator.provideBoardsRepository(this)

    companion object {
        private var instance: SudokuApp? = null
        fun appContext(): Context {
            return instance!!.applicationContext
        }
    }

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
    }
}