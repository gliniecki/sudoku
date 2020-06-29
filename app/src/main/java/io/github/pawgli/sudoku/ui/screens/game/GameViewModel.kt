package io.github.pawgli.sudoku.ui.screens.game

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.github.pawgli.sudoku.R
import io.github.pawgli.sudoku.models.Board
import io.github.pawgli.sudoku.models.OnBoardStateChanged
import io.github.pawgli.sudoku.models.asDomainModel
import io.github.pawgli.sudoku.network.SudokuApi
import io.github.pawgli.sudoku.utils.CallbackEvent
import io.github.pawgli.sudoku.utils.Event
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

    private val _notes = MutableLiveData<Map<Int, Set<Int>>>()
    val notes: LiveData<Map<Int, Set<Int>>>
        get() = _notes

    private val _initialIndexes = MutableLiveData<List<Int>>()
    val initialIndexes: LiveData<List<Int>>
        get() = _initialIndexes

    private val _highlightedNumbersIndexes = MutableLiveData<List<Int>>()
    val highlightedNumbersIndexes: LiveData<List<Int>>
        get() = _highlightedNumbersIndexes

    private val _displayPosNegDialog = MutableLiveData<CallbackEvent<Pair<Int, Int>>>()
    val displayPosNegDialog: LiveData<CallbackEvent<Pair<Int, Int>>>
        get() = _displayPosNegDialog

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
        moves.clear()
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
        if (isNotingActive.value == true) board.changeNote(currentIndex, number)
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
        val titleResId = if (board.isSolvedCorrectly()) {
            R.string.dialog_title_you_won
        } else {
            R.string.dialog_title_something_wrong
        }
        val messageResId = R.string.dialog_message_load_new_board
        showPosNegDialog(titleResId, messageResId) { fetchBoard() }
    }

    private fun showPosNegDialog(titleResId: Int, messageResId: Int, onPositive: () -> Unit) {
        _displayPosNegDialog.value =
            CallbackEvent(Pair(titleResId, messageResId)) { onPositive.invoke() }
    }

    fun onClearCellClicked() { board.clearCell(currentIndex) }

    fun onClearBoardClicked() {
        val titleId = R.string.dialog_title_are_you_sure
        val messageId = R.string.dialog_message_lose_progress
        showPosNegDialog(titleId, messageId) { board.clear() }
    }

    fun onNewBoardClicked() {
        val titleId = R.string.dialog_title_are_you_sure
        val messageId = R.string.dialog_message_lose_progress
        showPosNegDialog(titleId, messageId) { fetchBoard() }
    }

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
        _notes.value = board.getAllNotes()
    }
}