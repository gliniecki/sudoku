package io.github.pawgli.sudoku.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Board (
    val difficulty: String,
    val size: Int,
    val cells: List<Cell>) : Parcelable {

    fun getCell(row: Int, column: Int) = cells[row * size + column]
}