package io.github.pawgli.sudoku.models

data class NetworkBoard(val board: List<List<Int>>)

fun NetworkBoard.asDomainModel(difficulty: String): Board {
    val cells = mutableListOf<Cell>()
    for (row in board) {
        for (number in row) {
            val isInitial = number != 0
            cells.add(Cell(number, isInitial))
        }
    }
    return Board(difficulty, board.size, cells)
}