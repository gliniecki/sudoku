package io.github.pawgli.sudoku.models

import com.squareup.moshi.JsonClass


const val EMPTY_CELL = 0

@JsonClass(generateAdapter = true)
class Cell(
    var number: Int,
    val isInitial: Boolean,
    val notes: MutableSet<Int> = mutableSetOf())