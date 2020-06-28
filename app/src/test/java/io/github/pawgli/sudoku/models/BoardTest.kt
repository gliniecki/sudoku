package io.github.pawgli.sudoku.models

import junit.framework.Assert.assertEquals
import junit.framework.Assert.assertTrue
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

private const val DIFFICULTY = "easy"
private const val BOARD_SIZE = 9

class BoardTest {

    private val cells = mutableListOf<Cell>()
    private lateinit var board: Board

    private val numbersInitialState = listOf<Int>(
        0, 0, 1, 0, 0, 0, 0, 0, 0,
        0, 0, 0, 0, 0, 0, 0, 0, 0,
        0, 8, 9, 0, 0, 0, 1, 4, 5,
        1, 2, 3, 0, 5, 8, 9, 0, 7,
        0, 5, 0, 0, 0, 9, 0, 1, 0,
        0, 0, 7, 0, 0, 0, 3, 5, 0,
        3, 0, 2, 5, 9, 4, 8, 7, 6,
        5, 0, 8, 0, 0, 0, 4, 9, 1,
        0, 7, 0, 8, 6, 0, 5, 0, 2)

    private val numbersCorrectlySolved = listOf<Int>(
        6, 4, 1, 9, 8, 5, 7, 2, 3,
        2, 3, 5, 1, 4, 7, 6, 8, 9,
        7, 8, 9, 2, 3, 6, 1, 4, 5,
        1, 2, 3, 4, 5, 8, 9, 6, 7,
        4, 5, 6, 3, 7, 9, 2, 1, 8,
        8, 9, 7, 6, 1, 2, 3, 5, 4,
        3, 1, 2, 5, 9, 4, 8, 7, 6,
        5, 6, 8, 7, 2, 3, 4, 9, 1,
        9, 7, 4, 8, 6, 1, 5, 3, 2)


    @Before
    fun init() {
        setCellsInitial()
        initBoard()
    }

    private fun setCellsInitial() {
        cells.clear()
        numbersInitialState.forEach {
            val isInitial = it != EMPTY_CELL
            cells.add(Cell(it, isInitial))
        }
    }

    private fun setCellsSolved() {
        cells.clear()
        numbersCorrectlySolved.forEach {
            cells.add(Cell(it, false))
        }
    }

    private fun initBoard() {
        board = Board(DIFFICULTY, BOARD_SIZE, cells)
    }

    @Test
    fun setNumberTest_setValueOfEmptyCell() {
        val index = 0
        val newValue = 10
        board.setNumber(index, newValue)
        assertEquals(board.getNumber(index), newValue)
    }

    @Test
    fun setNumberTest_setValueOfInitialCell() {
        val index = 2
        val newValue = 10
        board.setNumber(index, newValue)
        assertNotEquals(board.getNumber(index), newValue)
    }

    @Test
    fun setNumberTest_setValueOfCellContainingNotes() {}

    @Test
    fun addNoteTest_addNoteToEmptyCell() {}

    @Test
    fun addNoteTest_addNoteToCellContainingSameNote() {}

    @Test
    fun addNoteTest_addNoteToCellContainingAddedNumber() {}

    @Test
    fun addNoteTest_addNoteToCellContainingInitialNumber() {}

    @Test
    fun getNumberTest_emptyCell() {
        assertEquals(board.getNumber(0), EMPTY_CELL)
    }

    @Test
    fun getNumber_cellContainingNumber() {
        assertEquals(board.getNumber(2), 1)
    }

    @Test
    fun getNumberTest_cellContainingNotes() {
        // TODO: add notes, check if equals empty_cell
    }

    @Test
    fun getAllNotesTest() { }

    @Test
    fun getInitialIndexesTest() {
        val initialIndexes = listOf<Int>(
            2, 19, 20, 24, 25, 26, 27, 28, 29, 31,
            32, 33, 35, 37, 41, 43, 47, 51, 52, 54,
            56, 57, 58, 59, 60, 61, 62, 63, 65, 69,
            70, 71, 73, 75, 76, 78, 80)
        assertArrayEquals(board.getInitialIndexes().toIntArray(), initialIndexes.toIntArray())
    }

    @Test
    fun getIndexesWithSameNumberTest_cellContainingNumber() {
        val selectedIndex = 26
        val indexesWithSameNumber = listOf<Int>(26, 31, 37, 52, 57, 63, 78)
        assertArrayEquals(
            board.getIndexesWithSameNumber(selectedIndex).toIntArray(),
            indexesWithSameNumber.toIntArray())
    }

    @Test
    fun getIndexesWithSameNumberTest_cellContainingNotes() {
        // TODO: check if the list is empty
    }

    @Test
    fun getIndexesWithSameNumberTest_emptyCell() {
        assertTrue(board.getIndexesWithSameNumber(0).isEmpty())
    }

    @Test
    fun clearBoardTest_numbersAdded() {
        board.setNumber(0, 10)
        board.setNumber(1, 10)
        board.clear()
        assertArrayEquals(board.getAllNumbers().toIntArray(), numbersInitialState.toIntArray())
    }

    @Test
    fun clearBoardTest_notesAdded() { }

    @Test
    fun clearCellTest_cellContainingNumber() {
        val newNumber = 10
        val index = 0
        board.setNumber(index, newNumber)
        board.clearCell(index)
        assertEquals(board.getNumber(index), EMPTY_CELL)
    }

    @Test
    fun clearCellTest_cellContainingNotes() {

    }

    @Test
    fun isFullTest_boardNotFull() {
        assertFalse(board.isFull())
    }

    @Test
    fun isFullTest_boardFull() {
        setCellsSolved()
        initBoard()
        assertTrue(board.isFull())
    }
}