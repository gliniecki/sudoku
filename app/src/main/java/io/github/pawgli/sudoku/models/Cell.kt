package io.github.pawgli.sudoku.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

const val EMPTY_CELL = 0

@Parcelize
class Cell(
    var number: Int,
    val isInitial: Boolean,
    val notes: MutableSet<Int> = mutableSetOf()) : Parcelable