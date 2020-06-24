package io.github.pawgli.sudoku

import android.app.Application
import timber.log.Timber

class SudokuApp : Application() {

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
    }
}