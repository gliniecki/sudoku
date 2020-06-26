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
                _boardFetchStatus.value = STATUS_SUCCESS
                initNumbers()
            } catch (t: Throwable) {
                Timber.w("Failed fetching the board: ${t.message}")
                _boardFetchStatus.value = STATUS_FAILURE
            }
        }
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
        board.getCell(selectedRow, selectedColumn).number = number
        _numbers.value?.set(getIndex(selectedRow, selectedColumn), number)
        _numbers.notifyObservers()
    }

    fun onNotesClicked() { _isNotingActive.value = !isNotingActive.value!! }

    fun onUndoClicked() {
        Timber.d("Undo clicked")
    }

    fun onCheckClicked() {
        Timber.d("Check clicked")
    }

    fun onTryAgainClicked() { fetchBoard() }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}

fun <T> MutableLiveData<T>.notifyObservers() {
    this.value = this.value
}