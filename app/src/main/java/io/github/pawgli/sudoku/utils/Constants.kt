package io.github.pawgli.sudoku.utils

enum class Difficulty(val value: String) {
    EASY("easy"),
    MEDIUM("medium"),
    HARD("hard")
}

const val KEY_UNFINISHED_GAME = "unfinished_game"