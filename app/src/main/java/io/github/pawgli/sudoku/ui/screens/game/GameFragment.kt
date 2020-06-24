package io.github.pawgli.sudoku.ui.screens.game

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import io.github.pawgli.sudoku.R
import io.github.pawgli.sudoku.databinding.FragmentGameBinding

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
        val gameMode = GameFragmentArgs.fromBundle(requireArguments()).gameMode
        val viewModelFactory = GameViewModelFactory(gameMode)
        viewModel = ViewModelProvider(this, viewModelFactory).get(GameViewModel::class.java)
    }

    private fun setUpBinding() {
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
    }

    private fun observeViewModel() {

    }
}