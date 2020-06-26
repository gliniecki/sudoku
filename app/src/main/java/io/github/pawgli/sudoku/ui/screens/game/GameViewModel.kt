package io.github.pawgli.sudoku.ui.screens.game

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.github.pawgli.sudoku.models.Board
import io.github.pawgli.sudoku.models.asDomainModel
import io.github.pawgli.sudoku.network.SudokuApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.*

const val STATUS_FETCHING = "fetching"
const val STATUS_SUCCESS = "success"
const val STATUS_FAILURE = "failure"
private const val NONE_SELECTED = -1
private const val EMPTY_CELL = 0

class GameViewModel(private val difficulty: String) : ViewModel() {

    private var viewModelJob = Job()
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    lateinit var board: Board
        private set
    private val movesIndexes = Stack<Int>()

    private var selectedRow = NONE_SELECTED
    private var selectedColumn = NONE_SELECTED

    private val _boardFetchStatus = MutableLiveData<String>()
    val boardFetchStatus: LiveData<String>
        get() = _boardFetchStatus

    private val _isNotingActive = MutableLiveData<Boolean>()
    val isNotingActive: LiveData<Boolean>
        get() = _isNotingActive

    private val _isBoardFull = MutableLiveData<Boolean>()
    val isBoardFull: LiveData<Boolean>
        get() = _isBoardFull

    private val _isUndoEnabled = MutableLiveData<Boolean>()
    val isUndoEnabled: LiveData<Boolean>
        get() = _isUndoEnabled

    private val _selectedCell = MutableLiveData<Pair<Int, Int>>()
    val selectedCell: LiveData<Pair<Int, Int>>
        get() = _selectedCell

    private val _numbers = MutableLiveData<MutableList<Int>>()
    val numbers: LiveData<MutableList<Int>>
        get() = _numbers

    private val _initialIndexes = MutableLiveData<MutableList<Int>>()
    val initialIndexes: LiveData<MutableList<Int>>
        get() = _initialIndexes

    init {
        initLiveDataObjects()
        fetchBoard()
    }

    private fun initLiveDataObjects() {
        _isNotingActive.value = false
        _isBoardFull.value = false
        _isUndoEnabled.value = false
        _numbers.value = mutableListOf()
        _initialIndexes.value = mutableListOf()
    }

    private fun fetchBoard() {
        _boardFetchStatus.value = STATUS_FETCHING
        coroutineScope.launch {
            val getBoardDeferred = SudokuApi.service.getBoardAsync(difficulty)
            try {
                val networkBoard = getBoardDeferred.await()
                board = networkBoard.asDomainModel(difficulty)
                onBoardFetched()
            } catch (t: Throwable) {
                Timber.w("Failed fetching the board: ${t.message}")
                _boardFetchStatus.value = STATUS_FAILURE
            }
        }
    }
    
    private fun onBoardFetched() {
        _boardFetchStatus.value = STATUS_SUCCESS
        initNumbers()
    }

    private fun initNumbers() {
        for (row in 0 until board.size) {
            for (column in 0 until board.size) {
                val number = board.getCell(row, column).number
                _numbers.value?.add(number)
                if (number != EMPTY_CELL) _initialIndexes.value?.add(getIndex(row, column))
            }
        }
        _initialIndexes.notifyObservers()
        _numbers.notifyObservers()
    }

    private fun getIndex(row: Int, column: Int) = board.size * row + column

    fun onCellClicked(row: Int, column: Int) {
        selectedRow = row
        selectedColumn = column
        _selectedCell.value = Pair(row, column)
    }

    fun onNumberClicked(number: Int) {
        if (selectedRow == NONE_SELECTED || selectedColumn == NONE_SELECTED) return
        if (isNotingActive.value == true) addNote(number)
        else addNumber(number)
    }

    fun onNotesClicked() { _isNotingActive.value = !isNotingActive.value!! }

    fun onUndoClicked() {
        val lastIndex = movesIndexes.last()
        movesIndexes.pop()
        removeNumber(lastIndex)
        if (movesIndexes.empty()) _isUndoEnabled.value = false
    }

    fun onCheckClicked() {
        Timber.d("Check clicked")
    }

    fun onTryAgainClicked() { fetchBoard() }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }


    private fun addNumber(number: Int) {
        val index = getIndex(selectedRow, selectedColumn)
        board.getCell(index).number = number
        movesIndexes.push(index)
        _numbers.value?.set(index, number)
        _numbers.notifyObservers()
        _isUndoEnabled.value = true
    }

    private fun removeNumber(index: Int) {
        board.getCell(index).number = EMPTY_CELL
        _numbers.value?.set(index, EMPTY_CELL)
        _numbers.notifyObservers()
    }

    private fun addNote(number: Int) {}
}

fun <T> MutableLiveData<T>.notifyObservers() {
    this.value = this.value
}