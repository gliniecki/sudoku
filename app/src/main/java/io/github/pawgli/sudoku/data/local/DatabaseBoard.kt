package io.github.pawgli.sudoku.data.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import io.github.pawgli.sudoku.models.Board
import io.github.pawgli.sudoku.models.Cell
import java.util.*

@Entity(tableName = "table_boards")
data class DatabaseBoard constructor(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var boardId: Long = 0,

    var difficulty: String,
    var size: Int,

    @TypeConverters(CellsConverter::class)
    var cells: List<Cell>,

    @ColumnInfo(name = "is_saved_by_user")
    var isSavedByUser: Boolean,

    @ColumnInfo(name = "updated_at")
    @TypeConverters(DateConverter::class)
    var updatedAt: Date = Date(System.currentTimeMillis())
)

fun DatabaseBoard.asDomainModel() = Board(this.difficulty, this.size, this.cells)

fun List<DatabaseBoard>.asDomainModel(): List<Board> {
    return map {
        Board(it.difficulty, it.size, it.cells)
    }
}
