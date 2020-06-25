package io.github.pawgli.sudoku.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Cell(
    var number: Int,
    val isInitial: Boolean,
    val notes: MutableSet<Int> = mutableSetOf()) : Parcelable