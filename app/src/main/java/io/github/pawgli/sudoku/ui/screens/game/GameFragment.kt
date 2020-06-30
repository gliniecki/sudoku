package io.github.pawgli.sudoku.ui.screens.game

import android.os.Bundle
import android.os.Handler
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import io.github.pawgli.sudoku.R
import io.github.pawgli.sudoku.databinding.FragmentGameBinding
import io.github.pawgli.sudoku.ui.dialogs.getPosNegDialog
import kotlinx.android.synthetic.main.fragment_game.*
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
        setHasOptionsMenu(true)
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
        observeNotes()
        observeHighlightedNumbersIndexes()
        observeDisplayPosNegDialog()
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
        setUpGameBoard()
    }

    private fun setGameLayoutVisibility(isVisible: Boolean) {
        val visibility = if (isVisible) View.VISIBLE else View.GONE
        binding.boardView.visibility = visibility
        binding.gameButtons.visibility = visibility
        binding.clearButtons.visibility = visibility
        if (isVisible) {
            setLoadingLayoutVisibility(isVisible = false)
            setFetchFailureLayoutVisibility(isVisible = false)
        }
    }

    private fun setUpGameBoard() {
        setBoardParameters()
        observeBoard()
    }

    private fun setBoardParameters() {
        context?.let {
            boardView.selectedCellColor =
                ContextCompat.getColor(it, R.color.colorActiveCell)
            boardView.highlightedCellColor =
                ContextCompat.getColor(it, R.color.colorHighlightedCell)
            boardView.highlightedNumberColor =
                ContextCompat.getColor(it, R.color.colorHighlightedNumber)
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
                if (it == null || it.isEmpty()) return@Observer
                try {
                    boardView.setNumbers(it)
                } catch (e: IllegalArgumentException) {
                    e.printStackTrace()
                }
            }
        )
    }

    private fun observeNotes() {
        viewModel.notes.observe(viewLifecycleOwner,
            Observer { boardView.setNotes(it) }
        )
    }

    private fun observeHighlightedNumbersIndexes() {
        viewModel.highlightedNumbersIndexes.observe(viewLifecycleOwner,
        Observer { boardView.setHighlightedNumbersIndexes(it) })
    }

    private fun observeDisplayPosNegDialog() {
        viewModel.displayPosNegDialog.observe(viewLifecycleOwner,
            Observer {
                val dialogParametersResIds = it.getContentIfNotHandled()
                if (dialogParametersResIds != null) {
                    val title = getString(dialogParametersResIds.first)
                    val message = getString(dialogParametersResIds.second)
                    getPosNegDialog(requireContext(), title, message, it.callback).show()
                }
            }
        )
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_game, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_item_reload -> viewModel.onNewBoardClicked()
            R.id.menu_item_exit -> closeApp()
            android.R.id.home -> findNavController().navigateUp()
        }
        return true
    }

    private fun closeApp() {
        activity?.moveTaskToBack(true)
        activity?.finish()
    }
}