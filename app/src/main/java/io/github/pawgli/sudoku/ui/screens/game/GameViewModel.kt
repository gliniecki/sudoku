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

class GameViewModel(private val difficulty: String) : ViewModel() {

    private var viewModelJob = Job()
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    lateinit var board: Board
        private set

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
                _boardFetchStatus.value = STATUS_SUCCESS
            } catch (t: Throwable) {
                Timber.w("Failed fetching the board: ${t.message}")
                _boardFetchStatus.value = STATUS_FAILURE
            }
        }
    }

    fun onCellClicked(row: Int, column: Int) {
        _selectedCell.value = Pair(row, column)
    }

    fun onNumberClicked(number: Int) {
        Timber.d("Number $number clicked")
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