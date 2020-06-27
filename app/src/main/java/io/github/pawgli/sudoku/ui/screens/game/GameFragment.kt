package io.github.pawgli.sudoku.ui.screens.game

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import io.github.pawgli.sudoku.R
import io.github.pawgli.sudoku.databinding.FragmentGameBinding
import kotlinx.android.synthetic.main.fragment_game.*
import timber.log.Timber
import java.lang.IllegalArgumentException

class GameFragment : Fragment() {

    private lateinit var binding: FragmentGameBinding
    private lateinit var viewModel: GameViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        (activity as AppCompatActivity).supportActionBar?.title = getString(R.string.app_name)
        binding = FragmentGameBinding.inflate(inflater)
        initViewModel()
        setUpBinding()
        observeViewModel()
        return binding.root
    }

    private fun initViewModel() {
        val difficulty = GameFragmentArgs.fromBundle(requireArguments()).difficulty
        val viewModelFactory = GameViewModelFactory(difficulty)
        viewModel = ViewModelProvider(this, viewModelFactory).get(GameViewModel::class.java)
    }

    private fun setUpBinding() {
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
    }

    private fun observeViewModel() {
        observeBoardFetchStatus()
        observeSelectedCell()
        observeInitialIndexes()
        observeNumbers()
        observeMessage()
    }

    private fun observeBoardFetchStatus() {
        viewModel.boardFetchStatus.observe(viewLifecycleOwner,
            Observer {
                when (it) {
                    STATUS_FETCHING -> setLoadingLayoutVisibility(isVisible = true)
                    STATUS_FAILURE -> setFetchFailureLayoutVisibility(isVisible = true)
                    STATUS_SUCCESS -> initGameLayout()
                }
            }
        )
    }

    private fun setLoadingLayoutVisibility(isVisible: Boolean) {
        val visibility = if (isVisible) View.VISIBLE else View.GONE
        binding.progressBar.visibility = visibility
        if (isVisible) setGameLayoutVisibility(isVisible = false)
    }

    private fun setFetchFailureLayoutVisibility(isVisible: Boolean) {
        val visibility = if (isVisible) View.VISIBLE else View.GONE
        binding.errorMessageDisplay.visibility = visibility
        binding.tryAgainButton.visibility = visibility
        if (isVisible) {
            setGameLayoutVisibility(isVisible = false)
            val handler = Handler()
            val animationTimeMillis = 1000L
            handler.postDelayed({
                setLoadingLayoutVisibility(isVisible = false) // Delayed to avoid a jumpy behavior of the animation when user tries to reload the board
            }, animationTimeMillis)
        }
    }

    private fun initGameLayout() {
        binding.boardView.boardSize = viewModel.board.size
        setGameLayoutVisibility(isVisible = true)
        observeBoard()
    }

    private fun setGameLayoutVisibility(isVisible: Boolean) {
        val visibility = if (isVisible) View.VISIBLE else View.GONE
        binding.boardView.visibility = visibility
        binding.gameButtons.visibility = visibility
        if (isVisible) {

            setLoadingLayoutVisibility(isVisible = false)
            setFetchFailureLayoutVisibility(isVisible = false)
        }
    }

    private fun observeBoard() {
        binding.boardView.setOnCellClickedListener {
                row, column -> viewModel.onCellClicked(row, column)
        }
    }

    private fun observeSelectedCell() {
        viewModel.selectedCell.observe(viewLifecycleOwner,
            Observer { boardView.setSelectedCell(it.first, it.second) })
    }

    private fun observeInitialIndexes() {
        viewModel.initialIndexes.observe(viewLifecycleOwner,
            Observer { boardView.setInitialIndexes(it) })
    }

    private fun observeNumbers() {
        viewModel.numbers.observe(viewLifecycleOwner,
            Observer {
                if (it == null || it.size == 0) return@Observer
                try {
                    boardView.setNumbers(it)
                } catch (e: IllegalArgumentException) {
                    e.printStackTrace()
                }
            }
        )
    }

    private fun observeMessage() {
        viewModel.message.observe(viewLifecycleOwner,
            Observer { Toast.makeText(activity, it, Toast.LENGTH_SHORT).show() })
    }
}