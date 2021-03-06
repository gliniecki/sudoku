package io.github.pawgli.sudoku.models

import junit.framework.Assert.assertEquals
import junit.framework.Assert.assertTrue
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import java.lang.IndexOutOfBoundsException

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
        assertEquals(newValue, board.getNumber(index))
    }

    @Test
    fun setNumberTest_setValueOfInitialCell() {
        val index = 2
        val newValue = 10
        board.setNumber(index, newValue)
        assertNotEquals(newValue, board.getNumber(index))
    }

    @Test
    fun setNumberTest_setValueOfCellContainingNotes() {
        val index = 0
        val note = 1
        val number = 2
        board.updateNote(index, note)
        board.setNumber(index, number)
        assertEquals(number, board.getNumber(index))
    }

    @Test
    fun setNumberTest_indexOutOfBoundsToLow() {
        val index = -1
        val number = 10
        board.setNumber(index, number)
        assertEquals(numbersInitialState, board.getAllNumbers())
    }

    @Test
    fun setNumberTest_indexOutOfBoundsToHigh() {
        val index = numbersInitialState.size
        val number = 10
        board.setNumber(index, number)
        assertEquals(numbersInitialState, board.getAllNumbers())
    }

    @Test
    fun updateNoteTest_addNoteToEmptyCell() {
        val index = 0
        val note = 1
        board.updateNote(index, note)
        assertTrue(board.getNotes(index).contains(note))
    }

    @Test
    fun updateNoteTest_addNoteToCellContainingSameNote() {
        val index = 0
        val note = 1
        board.updateNote(index, note)
        board.updateNote(index, note)
        assertTrue(board.getNotes(index).isEmpty())
    }

    @Test
    fun updateNoteTest_addNoteToCellContainingAddedNumber() {
        val index = 0
        val number = 1
        val note = 2
        board.setNumber(index, number)
        board.updateNote(index, note)
        assertTrue(board.getNotes(index).contains(note))
    }

    @Test
    fun updateNoteTest_addNoteToCellContainingInitialNumber() {
        val index = 2
        val note = 9
        board.updateNote(index, note)
        assertFalse(board.getNotes(index).contains(note))
    }

    @Test
    fun getNumberTest_emptyCell() {
        assertEquals(EMPTY_CELL, board.getNumber(0))
    }

    @Test
    fun getNumber_cellContainingNumber() {
        assertEquals(1, board.getNumber(2))
    }

    @Test
    fun getNumberTest_cellContainingNotes() {
        val index = 0
        val note = 1
        board.updateNote(index, note)
        assertEquals(EMPTY_CELL, board.getNumber(index))
    }

    @Test(expected = IndexOutOfBoundsException::class)
    fun getNumberTest_cellOutOfBoundsOneToLow() {
        board.getNumber(-1)
    }

    @Test(expected = IndexOutOfBoundsException::class)
    fun getNumberTest_cellOutOfBoundsOneToHigh() {
        board.getNumber(numbersInitialState.size)
    }

    @Test
    fun getNotesTest_emptyCell() {
        assertTrue(board.getNotes(0).isEmpty())
    }

    @Test
    fun getNotesTest_cellContainingNotes() {
        val index = 0
        val note1 = 1
        val note2 = 6
        board.updateNote(index, note1)
        board.updateNote(index, note2)
        assertEquals(board.getNotes(index), setOf(note1, note2))
    }

    @Test(expected = IndexOutOfBoundsException::class)
    fun getNotesTest_cellOutOfBoundsOneToLow() {
        board.getNotes(-1)
    }

    @Test(expected = IndexOutOfBoundsException::class)
    fun getNotesTest_cellOutOfBoundsOneToHigh() {
        board.getNotes(numbersInitialState.size)
    }

    @Test
    fun getAllNotesTest() {
        val index1 = 0
        val index2 = 1
        val noteSet1 = setOf<Int>(2, 3, 6)
        val noteSet2 = setOf<Int>(4, 1, 7)
        val notes = mutableMapOf<Int, Set<Int>>()
        notes[index1] = noteSet1
        notes[index2] = noteSet2
        noteSet1.forEach { board.updateNote(index1, it) }
        noteSet2.forEach { board.updateNote(index2, it) }
        assertTrue(board.getAllNotes().equals(notes))
    }

    @Test
    fun getInitialIndexesTest() {
        val initialIndexes = listOf<Int>(
            2, 19, 20, 24, 25, 26, 27, 28, 29, 31,
            32, 33, 35, 37, 41, 43, 47, 51, 52, 54,
            56, 57, 58, 59, 60, 61, 62, 63, 65, 69,
            70, 71, 73, 75, 76, 78, 80)
        assertArrayEquals(initialIndexes.toIntArray(), board.getInitialIndexes().toIntArray())
    }

    @Test
    fun getIndexesWithSameNumberTest_cellContainingNumber() {
        val selectedIndex = 26
        val indexesWithSameNumber = listOf<Int>(26, 31, 37, 52, 57, 63, 78)
        assertArrayEquals(
            indexesWithSameNumber.toIntArray(),
            board.getIndexesWithSameNumber(selectedIndex).toIntArray())
    }

    @Test
    fun getIndexesWithSameNumberTest_cellContainingNotes() {
        val index = 0
        val note = 1
        board.updateNote(index, note)
        assertTrue(board.getIndexesWithSameNumber(index).isEmpty())
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
        assertArrayEquals(numbersInitialState.toIntArray(), board.getAllNumbers().toIntArray())
    }

    @Test
    fun clearBoardTest_notesAdded() {
        val index = 0
        val note = 1
        board.updateNote(index, note)
        board.clear()
        assertTrue(board.getNotes(index).isEmpty())
    }

    @Test
    fun clearCellTest_cellContainingNumber() {
        val newNumber = 10
        val index = 0
        board.setNumber(index, newNumber)
        board.clearCell(index)
        assertEquals(EMPTY_CELL, board.getNumber(index))
    }

    @Test
    fun clearCellTest_cellContainingNotes() {
        val index = 0
        val note = 1
        board.updateNote(index, note)
        board.clearCell(index)
        assertTrue(board.getNotes(index).isEmpty())
    }

    @Test
    fun isEmptyTest_boardContainingAddedNumber() {
        board.setNumber(0, 1)
        assertFalse(board.isEmpty())
    }

    @Test
    fun isEmptyTest_boardContainingNote() {
        board.updateNote(0, 4)
        assertFalse(board.isEmpty())
    }

    @Test
    fun isEmptyTest_boardEmpty() {
        assertTrue(board.isEmpty())
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