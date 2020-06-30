package io.github.pawgli.sudoku.ui.screens.main_menu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import io.github.pawgli.sudoku.R
import io.github.pawgli.sudoku.databinding.FragmentMainMenuBinding

class MainMenuFragment : Fragment() {

    private lateinit var binding: FragmentMainMenuBinding
    private val viewModel: MainMenuViewModel by lazy {
        ViewModelProvider(this).get(MainMenuViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        (activity as AppCompatActivity).supportActionBar?.title = getString(R.string.app_name)
        binding = FragmentMainMenuBinding.inflate(inflater)
        setUpBinding()
        observeViewModel()
        return binding.root
    }

    private fun setUpBinding() {
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
    }

    private fun observeViewModel() {
        observeChosenOption()
    }

    private fun observeChosenOption() {
        viewModel.chosenOption.observe(viewLifecycleOwner,
            Observer {
                when(val chosenOption = it.getContentIfNotHandled()) {
                    null -> return@Observer
                    OPTION_EXIT -> closeApp()
                    else -> openGame(difficulty = chosenOption)
                }
            }
        )
    }

    private fun openGame(difficulty: String) {
        this.findNavController().navigate(MainMenuFragmentDirections.actionStartGame(difficulty))
    }

    private fun closeApp() {
        activity?.moveTaskToBack(true)
        activity?.finish()
    }
}