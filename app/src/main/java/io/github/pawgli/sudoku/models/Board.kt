package io.github.pawgli.sudoku.models

import android.os.Parcelable
import io.github.pawgli.sudoku.utils.isSudokuSolvedCorrectly
import kotlinx.android.parcel.IgnoredOnParcel
import kotlinx.android.parcel.Parcelize
import java.lang.IndexOutOfBoundsException

@Parcelize
class Board (
    val difficulty: String,
    val size: Int,
    private val cells: List<Cell>) : Parcelable {

    @IgnoredOnParcel
    private val onBoardStateChangedObservers = mutableListOf<OnBoardStateChanged>()

    fun setNumber(index: Int, number: Int) {
        if (index !in cells.indices) return
        val previousValue = cells[index].number
        if (!cells[index].isInitial && previousValue != number) {
            removeNotes(index)
            cells[index].number = number
            notifyNumberChanged(index, previousValue)
        }
    }

    private fun removeNumber(index: Int) {
        if (index !in cells.indices) return
        val previousValue = cells[index].number
        if (previousValue != EMPTY_CELL && !cells[index].isInitial) {
            cells[index].number = EMPTY_CELL
            notifyNumberChanged(index, previousValue)
        }
    }

    fun updateNote(index: Int, note: Int) {
        if (index !in cells.indices) return
        val cell = cells[index]
        if (note in 1..size && !cell.isInitial) {
            if (cell.notes.contains(note)) {
                cell.notes.remove(note)
            } else {
                removeNumber(index)
                cells[index].notes.add(note)
            }
            notifyNotesChanged()
        }
    }

    private fun removeNotes(index: Int) {
        if (index !in cells.indices) return
        cells[index].notes.clear()
        notifyNotesChanged()
    }

    fun getNumber(index: Int): Int {
        if (index in cells.indices) return cells[index].number
        else throw IndexOutOfBoundsException("cells.size = ${cells.size}, index = $index")
    }

    fun getAllNumbers(): List<Int> {
        val numbers = mutableListOf<Int>()
        cells.forEach {
            numbers.add(it.number)
        }
        return numbers
    }

    fun getNotes(index: Int): Set<Int> {
        if (index in cells.indices) return cells[index].notes
        else throw IndexOutOfBoundsException("cells.size = ${cells.size}, index = $index")
    }

    fun getAllNotes(): Map<Int, Set<Int>> {
        val notes = mutableMapOf<Int, Set<Int>>()
            cells.forEachIndexed {
                index, cell ->
                    if (cell.notes.isNotEmpty()) notes[index] = cell.notes
            }
        return notes
    }

    fun getInitialIndexes(): List<Int> {
        val initialIndexes = mutableListOf<Int>()
        cells.forEachIndexed {
                index, cell -> if (cell.isInitial) initialIndexes.add(index)
        }
        return initialIndexes
    }

    fun getIndexesWithSameNumber(index: Int): List<Int> {
        if (index !in cells.indices) {
            throw IndexOutOfBoundsException("cells.size = ${cells.size}, index = $index")
        }
        val number = cells[index].number
        val indexes = mutableListOf<Int>()
        if (number != EMPTY_CELL) {
            cells.forEachIndexed() {
                currentIndex, element -> if (element.number == number) indexes.add(currentIndex)
            }
        }
        return indexes
    }

    fun clearCell(index: Int) {
        if (index !in cells.indices) return
        removeNumber(index)
        removeNotes(index)
    }

    fun clear() {
        cells.forEach {
            if (!it.isInitial) {
                it.number = EMPTY_CELL
                it.notes.clear()
            }
        }
        notifyBoardCleared()
    }

    fun isEmpty(): Boolean {
        cells.forEach {
            val hasAddedNumber = !it.isInitial && it.number != EMPTY_CELL
            val hasNotes = it.notes.isNotEmpty()
            if (hasAddedNumber || hasNotes) return false
        }
        return true
    }

    fun isFull(): Boolean {
        cells.forEach {
            if (it.number == EMPTY_CELL) return false
        }
        return true
    }

    fun isSolvedCorrectly(): Boolean {
        if (!isFull()) return false
        return isSudokuSolvedCorrectly(getAllNumbers())
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


}

interface OnBoardStateChanged {
    fun onNumberChanged(index: Int, previousValue: Int)
    fun onBoardCleared()
    fun onNotesStateChanged()
}