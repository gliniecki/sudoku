package io.github.pawgli.sudoku.ui.screens.game

import androidx.lifecycle.*
import io.github.pawgli.sudoku.R
import io.github.pawgli.sudoku.data.repository.BoardsRepository
import io.github.pawgli.sudoku.data.repository.Result
import io.github.pawgli.sudoku.models.Board
import io.github.pawgli.sudoku.models.OnBoardStateChanged
import io.github.pawgli.sudoku.utils.CallbackEvent
import io.github.pawgli.sudoku.utils.Difficulty
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.*

private const val NONE_SELECTED = -1

class GameViewModel(
    private val difficulty: Difficulty,
    private val boardsRepository: BoardsRepository
) : ViewModel(), OnBoardStateChanged, LifecycleObserver {

    lateinit var board: Board
        private set

    private val moves = Stack<Pair<Int, Int>>()
    private var isRestoringPreviousState = false
    private var currentIndex = NONE_SELECTED

    private val _boardFetchStatus =
        boardsRepository.observeBoardResult().map { checkBoardResult(it) }
    val boardFetchStatus: LiveData<Status>
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

    private fun checkBoardResult(boardResult: Result<Board>): Status {
        return when(boardResult) {
            is Result.Loading -> Status.FETCHING
            is Result.Success -> {
                onBoardFetched(boardResult.data)
                Status.SUCCESS
            }
            is Result.Error -> {
                Timber.w("Failed fetching the board: ${boardResult.exception}")
                Status.FAILURE
            }
        }
    }

    private fun fetchBoard() {
        viewModelScope.launch {
            boardsRepository.getEmptyBoard(difficulty)
        }
    }

    private fun onBoardFetched(fetchedBoard: Board) {
        board = fetchedBoard
        initBoard()
    }

    private fun initBoard() {
        clearMoves()
        updateBoardState()
        board.addOnBoardStateChangedObserver(this)
    }

    private fun clearMoves() {
        moves.clear()
        _isUndoEnabled.value = false
    }

    private fun updateBoardState() {
        _initialIndexes.value = board.getInitialIndexes()
        _numbers.value = board.getAllNumbers()
        _notes.value = board.getAllNotes()
        if (currentIndex != NONE_SELECTED) {
            _highlightedNumbersIndexes.value = board.getIndexesWithSameNumber(currentIndex)
        }
    }

    fun onCellClicked(row: Int, column: Int) {
        currentIndex = getIndex(row, column)
        _selectedCell.value = Pair(row, column)
        _highlightedNumbersIndexes.value = board.getIndexesWithSameNumber(currentIndex)
    }

    private fun getIndex(row: Int, column: Int) = board.size * row + column

    fun onNumberClicked(number: Int) {
        if (currentIndex == NONE_SELECTED) return
        if (isNotingActive.value == true) board.updateNote(currentIndex, number)
        else addNumber(number)
    }

    private fun addNumber(number: Int) {
        board.setNumber(currentIndex, number)
        if (board.isFull()) _isBoardFull.value = true
    }

    fun onNotesClicked() { _isNotingActive.value = !isNotingActive.value!! }

    fun onUndoClicked() {
        if (moves.isEmpty()) _isUndoEnabled.value = false
        else restoreLastMove()
    }

    private fun restoreLastMove() {
        isRestoringPreviousState = true
        val lastMove = moves.last()
        moves.pop()
        if (moves.isEmpty()) _isUndoEnabled.value = false
        board.setNumber(index = lastMove.first, number = lastMove.second)
    }

    fun onCheckClicked() {
        val titleResId = if (board.isSolvedCorrectly()) {
            R.string.dialog_title_you_won
        } else {
            R.string.dialog_title_something_wrong
        }
        val messageResId = R.string.dialog_message_load_new_board
        displayPosNegDialog(titleResId, messageResId) { fetchBoard() }
    }

    private fun displayPosNegDialog(titleResId: Int, messageResId: Int, onPositive: () -> Unit) {
        _displayPosNegDialog.value =
            CallbackEvent(Pair(titleResId, messageResId)) { onPositive.invoke() }
    }

    fun onClearCellClicked() { board.clearCell(currentIndex) }

    fun onClearBoardClicked() {
        if (!board.isEmpty()) {
            val titleId = R.string.dialog_title_are_you_sure
            val messageId = R.string.dialog_message_lose_progress
            displayPosNegDialog(titleId, messageId) { board.clear() }
        }
    }

    fun onNewBoardClicked() {
        if (!this::board.isInitialized || board.isEmpty()) {
            fetchBoard()
        } else {
            val titleId = R.string.dialog_title_are_you_sure
            val messageId = R.string.dialog_message_lose_progress
            displayPosNegDialog(titleId, messageId) { fetchBoard() }
        }
    }

    override fun onNumberChanged(index: Int, previousValue: Int) {
        _isBoardFull.value = board.isFull()
        updateBoardState()
        if (isRestoringPreviousState) isRestoringPreviousState = false
        else addMove(index, previousValue)
    }

    private fun addMove(index: Int, number: Int) {
        moves.push(Pair(index, number))
        _isUndoEnabled.value = true
    }

    override fun onBoardCleared() {
        _isBoardFull.value = false
        clearMoves()
        updateBoardState()
    }

    override fun onNotesStateChanged() {
        _notes.value = board.getAllNotes()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    private fun onLifecycleStop() {

    }
}

enum class Status {
    FETCHING, SUCCESS, FAILURE
}