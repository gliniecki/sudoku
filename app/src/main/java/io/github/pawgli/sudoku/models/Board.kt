package io.github.pawgli.sudoku.models

import android.os.Parcelable
import io.github.pawgli.sudoku.ui.sqrt
import kotlinx.android.parcel.IgnoredOnParcel
import kotlinx.android.parcel.Parcelize

@Parcelize
class Board (
    val difficulty: String,
    val size: Int,
    private val cells: List<Cell>) : Parcelable {

    @IgnoredOnParcel
    private val onBoardStateChangedObservers = mutableListOf<OnBoardStateChanged>()

    fun setNumber(index: Int, number: Int) {
        val previousValue = cells[index].number
        if (!cells[index].isInitial && previousValue != number) {
            cells[index].number = number
            notifyNumberChanged(index, previousValue)
        }
    }

    fun setNumber(row: Int, column: Int, number: Int) {
        val index = getIndex(row, column)
        setNumber(index, column)
    }

    fun removeNumber(index: Int) {
        val previousValue = cells[index].number
        if (previousValue != EMPTY_CELL && !cells[index].isInitial) {
            cells[index].number = EMPTY_CELL
            notifyNumberChanged(index, previousValue)
        }
    }

    fun removeNumber(row: Int, column: Int) {
        removeNumber(getIndex(row, column))
    }

    fun addNote(index: Int, note: Int) {

    }

    fun addNote(row: Int, column: Int, note: Int) {

    }

    fun getCell(index: Int) = cells[index]

    fun getCell(row: Int, column: Int) = cells[getIndex(row, column)]

    private fun getIndex(row: Int, column: Int) = row * size + column

    fun getAllNumbers(): List<Int> {
        val numbers = mutableListOf<Int>()
        cells.forEach {
            numbers.add(it.number)
        }
        return numbers
    }

    fun getAllNotes(): List<Set<Int>> { return mutableListOf() }

    fun getInitialIndexes(): List<Int> {
        val initialIndexes = mutableListOf<Int>()
        cells.forEachIndexed {
                index, element -> if (element.isInitial) initialIndexes.add(index)
        }
        return initialIndexes
    }

    fun getIndexesWithSameNumber(index: Int): List<Int> {
        val number = cells[index].number
        val indexes = mutableListOf<Int>()
        if (number != EMPTY_CELL) {
            cells.forEachIndexed() {
                currentIndex, element -> if (element.number == number) indexes.add(currentIndex)
            }
        }
        return indexes
    }

    fun clear() {
        cells.forEach {
            if (!it.isInitial) {
                it.number = EMPTY_CELL
                // TODO: clear notes
            }
        }
        notifyBoardCleared()
    }

    fun isFull(): Boolean {
        cells.forEach {
            if (it.number == EMPTY_CELL) return false
        }
        return true
    }

    fun addOnBoardStateChangedObserver(observer: OnBoardStateChanged) {
        if (!onBoardStateChangedObservers.contains(observer)) {
            onBoardStateChangedObservers.add(observer)
        }
    }

    fun removeOnBoardStateChangedObserver(observer: OnBoardStateChanged) {
        onBoardStateChangedObservers.remove(observer)
    }

    private fun notifyNumberChanged(index: Int, previousValue: Int) {
        onBoardStateChangedObservers.forEach {
            it.onNumberChanged(index, previousValue)
        }
    }

    private fun notifyBoardCleared() {
        onBoardStateChangedObservers.forEach {
            it.onBoardCleared()
        }
    }

    private fun notifyNotesChanged() {
        onBoardStateChangedObservers.forEach {
            it.onNotesStateChanged()
        }
    }

    fun isSolvedCorrectly(): Boolean {
        if (!isFull()) return false
        return  hasDuplicatesHorizontally() && hasDuplicatesVertically() && hasDuplicatesInBoxes()
    }

    private fun hasDuplicatesHorizontally(): Boolean {
        val numbers = mutableListOf<Int>()
        val firstIndexOfLastRow = size * (size - 1)
        for (row in 0..firstIndexOfLastRow step size) {
            numbers.clear()
            for (currentIndex in row until row + size) {
                val currentNumber = cells[currentIndex].number
                if (numbers.contains(currentNumber)) return false
                else numbers.add(currentNumber)
            }
        }
        return true
    }

    private fun hasDuplicatesVertically(): Boolean {
        val numbers = mutableListOf<Int>()
        for (column in 0 until size) {
            numbers.clear()
            val lastIndexInColumn = column + size * (size - 1)
            for (index in column..lastIndexInColumn step size) {
                val currentNumber = cells[index].number
                if (numbers.contains(currentNumber)) return false
                else numbers.add(currentNumber)
            }
        }
        return true
    }

    private fun hasDuplicatesInBoxes(): Boolean {
        val numbers = mutableListOf<Int>()
        val boxSize = size.sqrt()
        val lastBoxFirstIndex = size * (size - boxSize) + size - boxSize
        var currentBoxFirstIndex = 0
        var iterationCounter = 0
        while (currentBoxFirstIndex <= lastBoxFirstIndex) {
            numbers.clear()
            for (columnFirstIndex in currentBoxFirstIndex until currentBoxFirstIndex + boxSize) {
                val columnLastIndex = columnFirstIndex + size * (boxSize - 1)
                for (currentIndex in columnFirstIndex..columnLastIndex step size) {
                    val currentNumber = cells[currentIndex].number
                    if (numbers.contains(currentNumber)) return false
                    else numbers.add(currentNumber)
                }
            }
            val nextStep = when (++iterationCounter % boxSize) {
                0 -> size * (boxSize - 1) + boxSize
                else -> boxSize
            }
            currentBoxFirstIndex += nextStep
        }
        return true
    }
}

interface OnBoardStateChanged {
    fun onNumberChanged(index: Int, previousValue: Int)
    fun onBoardCleared()
    fun onNotesStateChanged()
}