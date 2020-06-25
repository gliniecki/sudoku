package io.github.pawgli.sudoku.models

class Cell(number: Int, val isInitial: Boolean, val notes: MutableSet<Int> = mutableSetOf()) {

    var number: Int = number
        set(value) {
            if (!isInitial) field = value
        }
}