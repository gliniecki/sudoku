package io.github.pawgli.sudoku.utils

import junit.framework.Assert.assertFalse
import junit.framework.Assert.assertTrue
import org.junit.Test

class SudokuSolutionCheckTest {

    private val boardSize = 9

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

    private val numbersOneValueToLow = listOf<Int>(
        6, 4, 1, 9, 8, 5, 7, 2, 3,
        2, 3, 5, 1, 4, 7, 6, 8, 9,
        7, 8, 9, 2, 3, 6, 1, 4, 5,
        1, 2, 0, 4, 5, 8, 9, 6, 7,
        4, 5, 6, 3, 7, 9, 2, 1, 8,
        8, 9, 7, 6, 1, 2, 3, 5, 4,
        3, 1, 2, 5, 9, 4, 8, 7, 6,
        5, 6, 8, 7, 2, 3, 4, 9, 1,
        9, 7, 4, 8, 6, 1, 5, 3, 2)

    private val numbersOneValueToHigh = listOf<Int>(
        6, 4, 1, 9, 8, 5, 7, 2, 3,
        2, 3, 5, 1, 4, 7, 6, 8, 9,
        7, 8, 9, 2, 3, 6, 1, 4, 5,
        1, 2, 3, 4, 5, 10, 9, 6, 7,
        4, 5, 6, 3, 7, 9, 2, 1, 8,
        8, 9, 7, 6, 1, 2, 3, 5, 4,
        3, 1, 2, 5, 9, 4, 8, 7, 6,
        5, 6, 8, 7, 2, 3, 4, 9, 1,
        9, 7, 4, 8, 6, 1, 5, 3, 2)

    private val numbersDuplicateHorizontally = listOf<Int>(
        6, 4, 1, 9, 8, 5, 7, 2, 3,
        2, 3, 5, 1, 4, 7, 6, 8, 9,
        7, 8, 9, 2, 3, 6, 1, 4, 5,
        1, 2, 3, 4, 5, 8, 9, 6, 7,
        4, 5, 6, 0, 7, 9, 2, 1, 0,
        8, 9, 7, 6, 1, 2, 3, 5, 4,
        3, 1, 2, 5, 9, 4, 8, 7, 6,
        5, 6, 8, 7, 2, 3, 4, 9, 1,
        9, 7, 4, 8, 6, 1, 5, 3, 2)

    private val numbersDuplicateVertically = listOf<Int>(
        6, 4, 1, 9, 8, 5, 7, 2, 3,
        2, 3, 5, 1, 4, 7, 6, 8, 9,
        7, 8, 9, 2, 3, 6, 1, 4, 5,
        1, 2, 3, 4, 5, 8, 0, 6, 7,
        4, 5, 6, 3, 7, 9, 2, 1, 8,
        8, 9, 7, 6, 1, 2, 3, 5, 4,
        3, 1, 2, 5, 9, 4, 8, 7, 6,
        5, 6, 8, 7, 2, 3, 0, 9, 1,
        9, 7, 4, 8, 6, 1, 5, 3, 2)

    private val numbersDuplicateInBox = listOf<Int>(
        6, 4, 1, 9, 8, 5, 7, 2, 3,
        2, 3, 5, 1, 4, 7, 6, 8, 9,
        7, 0, 0, 2, 3, 6, 1, 4, 5,
        1, 2, 3, 4, 5, 8, 9, 6, 7,
        4, 5, 6, 3, 7, 9, 2, 1, 8,
        8, 9, 7, 6, 1, 2, 3, 5, 4,
        3, 1, 2, 5, 9, 4, 8, 7, 6,
        5, 6, 8, 7, 2, 3, 4, 9, 1,
        9, 7, 4, 8, 6, 1, 5, 3, 2)

    @Test
    fun isSudokuSolvedCorrectlyTest_correctlySolved() {
        assertTrue(isSudokuSolvedCorrectly(numbersCorrectlySolved))
    }

    @Test
    fun hasAllNumbersInRangeTest_allNumbersInRange() {
        assertTrue(hasAllNumbersInRange(numbersCorrectlySolved, boardSize))
    }

    @Test
    fun hasAllNumbersInRangeTest_oneValueToLow() {
        assertFalse(hasAllNumbersInRange(numbersOneValueToLow, boardSize))
    }

    @Test
    fun hasAllNumbersInRangeTest_oneValueToHigh() {
        assertFalse(hasAllNumbersInRange(numbersOneValueToHigh, boardSize))
    }

    @Test
    fun hasDuplicatesHorizontallyTest_noDuplicates() {
        assertTrue(hasDuplicatesHorizontally(numbersCorrectlySolved, boardSize))
    }

    @Test
    fun hasDuplicatesHorizontallyTest_oneDuplicate() {
        assertFalse(hasDuplicatesHorizontally(numbersDuplicateHorizontally, boardSize))
    }

    @Test
    fun hasDuplicatesVerticallyTest_noDuplicates() {
        assertTrue(hasDuplicatesVertically(numbersCorrectlySolved, boardSize))
    }

    @Test
    fun hasDuplicatesVerticallyTest_oneDuplicate() {
        assertFalse(hasDuplicatesVertically(numbersDuplicateVertically, boardSize))
    }

    @Test
    fun hasDuplicatesInBoxesTest_noDuplicates() {
        assertTrue(hasDuplicatesInBoxes(numbersCorrectlySolved, boardSize))
    }

    @Test
    fun hasDuplicatesInBoxesTest_oneDuplicate() {
        assertFalse(hasDuplicatesInBoxes(numbersDuplicateInBox, boardSize))
    }
}