package io.github.pawgli.sudoku.ui.screens.game

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.github.pawgli.sudoku.models.Board
import io.github.pawgli.sudoku.models.OnBoardStateChanged
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

class GameViewModel(private val difficulty: String) : ViewModel(), OnBoardStateChanged {

    private var viewModelJob = Job()
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    lateinit var board: Board
        private set

    private val moves = Stack<Pair<Int, Int>>()
    private var isRestoringPreviousState = false
    private var currentIndex = NONE_SELECTED

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

    private val _numbers = MutableLiveData<List<Int>>()
    val numbers: LiveData<List<Int>>
        get() = _numbers

    private val _initialIndexes = MutableLiveData<List<Int>>()
    val initialIndexes: LiveData<List<Int>>
        get() = _initialIndexes

    private val _highlightedNumbersIndexes = MutableLiveData<List<Int>>()
    val highlightedNumbersIndexes: LiveData<List<Int>>
        get() = _highlightedNumbersIndexes

    private val _message = MutableLiveData<String>()
    val message: LiveData<String>
        get() = _message

    init {
        initLiveDataObjects()
        fetchBoard()
    }

    private fun initLiveDataObjects() {
        _isNotingActive.value = false
        _isBoardFull.value = false
        _isUndoEnabled.value = false
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
        _initialIndexes.value = board.getInitialIndexes()
        _numbers.value = board.getAllNumbers()
        board.addOnBoardStateChangedObserver(this)
    }

    fun onCellClicked(row: Int, column: Int) {
        currentIndex = getIndex(row, column)
        _selectedCell.value = Pair(row, column)
        _highlightedNumbersIndexes.value = board.getIndexesWithSameNumber(currentIndex)
    }

    private fun getIndex(row: Int, column: Int) = board.size * row + column

    fun onNumberClicked(number: Int) {
        if (currentIndex == NONE_SELECTED) return
        if (isNotingActive.value == true) board.addNote(currentIndex, number)
        else addNumber(number)
    }

    private fun addNumber(number: Int) {
        board.setNumber(currentIndex, number)
        if (board.isFull()) _isBoardFull.value = true
    }

    fun onNotesClicked() { _isNotingActive.value = !isNotingActive.value!! }

    fun onUndoClicked() {
        restoreLastMove()
    }

    fun onCheckClicked() {
        if (board.isSolvedCorrectly()) _message.value = "You won!"
        else _message.value = "Check again!"
    }

    fun onClearCellClicked() {
        // TODO: Alert [You will lose your progress ok / cancel]
        board.clearCell(currentIndex)
    }

    fun onClearBoardClicked() {
        board.clear()
    }

    fun onTryAgainClicked() { fetchBoard() }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    override fun onNumberChanged(index: Int, previousValue: Int) {
        _isBoardFull.value = board.isFull()
        _numbers.value = board.getAllNumbers()
        _highlightedNumbersIndexes.value = board.getIndexesWithSameNumber(currentIndex)
        if (isRestoringPreviousState) isRestoringPreviousState = false
        else addMove(index, previousValue)
    }

    override fun onBoardCleared() {
        _isBoardFull.value = false
        clearMoves()
        _numbers.value = board.getAllNumbers()
    }

    private fun addMove(index: Int, number: Int) {
        moves.push(Pair(index, number))
        _isUndoEnabled.value = true
    }

    private fun restoreLastMove() {
        isRestoringPreviousState = true
        val lastMove = moves.last()
        moves.pop()
        if (moves.empty()) _isUndoEnabled.value = false
        board.setNumber(lastMove.first, lastMove.second)
    }

    private fun clearMoves() {
        moves.clear()
        _isUndoEnabled.value = false
    }

    override fun onNotesStateChanged() {
        TODO("Not yet implemented")
    }
}