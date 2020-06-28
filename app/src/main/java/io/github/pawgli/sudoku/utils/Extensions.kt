package io.github.pawgli.sudoku.utils

import kotlin.math.floor

fun Int.sqrt(): Int { return kotlin.math.sqrt(this.toDouble()).toInt() }

fun Int.isPerfectSquare(): Boolean {
    val sqrt = kotlin.math.sqrt(this.toDouble())
    val sqrtFloor = floor(sqrt)
    return sqrt == sqrtFloor
}