package io.github.pawgli.sudoku.data.local

import androidx.room.TypeConverter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import io.github.pawgli.sudoku.models.Cell
import java.util.*

class DateConverter {

    @TypeConverter
    fun toDate(timestamp: Long?) = if (timestamp == null) null else Date(timestamp)

    @TypeConverter
    fun toTimestamp(date: Date?) = date?.time
}

class CellsConverter {
    private val moshi by lazy {
        Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
    }
    private val type by lazy { Types.newParameterizedType(List::class.java, Cell::class.java) }
    private val jsonAdapter by lazy { moshi.adapter<List<Cell>>(type) }

    @TypeConverter
    fun toJson(cells: List<Cell>) = jsonAdapter.toJson(cells)!!

    @TypeConverter
    fun fromJson(cellsJson: String) = jsonAdapter.fromJson(cellsJson)
}