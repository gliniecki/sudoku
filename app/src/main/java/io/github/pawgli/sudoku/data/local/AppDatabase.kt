package io.github.pawgli.sudoku.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [DatabaseBoard::class], version = 1, exportSchema = false)
@TypeConverters(DateConverter::class, CellsConverter::class, DifficultyConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract val boardsDao: BoardsDao
}

private lateinit var INSTANCE: AppDatabase

fun getDatabase(context: Context): AppDatabase {
    synchronized(AppDatabase::class.java) {
        if (!::INSTANCE.isInitialized) {
            INSTANCE = Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "boards").build()
        }
    }
    return INSTANCE
}