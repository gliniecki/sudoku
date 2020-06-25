package io.github.pawgli.sudoku.models

class Board (val difficulty: String, private val size: Int, private val cells: List<Cell>) {
    fun getCell(row: Int, column: Int) = cells[row * size + column]
}