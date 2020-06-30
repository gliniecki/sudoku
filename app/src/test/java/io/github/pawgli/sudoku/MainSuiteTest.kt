package io.github.pawgli.sudoku

import io.github.pawgli.sudoku.models.BoardTest
import io.github.pawgli.sudoku.ui.screens.main_menu.MainMenuViewModelTest
import io.github.pawgli.sudoku.utils.SudokuSolutionCheckTest

import org.junit.runner.RunWith
import org.junit.runners.Suite

@RunWith(Suite::class)
@Suite.SuiteClasses(
    BoardTest::class,
    SudokuSolutionCheckTest::class,
    MainMenuViewModelTest::class
)
class MainSuiteTest