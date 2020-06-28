package io.github.pawgli.sudoku.models

import android.os.Parcelable
import io.github.pawgli.sudoku.utils.isSudokuSolvedCorrectly
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
            removeNotes(index)
            cells[index].number = number
            notifyNumberChanged(index, previousValue)
        }
    }

    private fun removeNumber(index: Int) {
        val previousValue = cells[index].number
        if (previousValue != EMPTY_CELL && !cells[index].isInitial) {
            cells[index].number = EMPTY_CELL
            notifyNumberChanged(index, previousValue)
        }
    }

    fun addNote(index: Int, note: Int) {
        val cell = cells[index]
        if (note in 1..size && !cell.notes.contains(note) && !cell.isInitial) {
            removeNumber(index)
            cells[index].notes.add(note)
            notifyNotesChanged()
        }
    }

    private fun removeNotes(index: Int) {
        cells[index].notes.clear()
        notifyNotesChanged()
    }

    fun getNumber(index: Int) = cells[index].number

    fun getAllNumbers(): List<Int> {
        val numbers = mutableListOf<Int>()
        cells.forEach {
            numbers.add(it.number)
        }
        return numbers
    }

    fun getNotes(index: Int) = cells[index].notes

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
                it.notes.clear()
            }
        }
        notifyBoardCleared()
    }

    fun clearCell(index: Int) {
        removeNumber(index)
        removeNotes(index)
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