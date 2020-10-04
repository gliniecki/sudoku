package io.github.pawgli.sudoku.data.remote

import io.github.pawgli.sudoku.models.Board
import io.github.pawgli.sudoku.models.Cell
import io.github.pawgli.sudoku.utils.Difficulty

data class NetworkBoard(val board: List<List<Int>>)

fun NetworkBoard.asDomainModel(difficulty: Difficulty): Board {
    val cells = mutableListOf<Cell>()
    for (row in board) {
        for (number in row) {
            val isInitial = number != 0
            cells.add(Cell(number, isInitial))
        }
    }
    return Board(difficulty, board.size, cells)
}