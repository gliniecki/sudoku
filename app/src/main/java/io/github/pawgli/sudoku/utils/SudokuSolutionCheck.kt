package io.github.pawgli.sudoku.utils

fun isSudokuSolvedCorrectly(numbers: List<Int>): Boolean {
    val boardSize = (numbers.size).sqrt()
    return  hasAllNumbersInRange(numbers, boardSize)
            && hasNoDuplicatesHorizontally(numbers, boardSize)
            && hasNoDuplicatesVertically(numbers, boardSize)
            && hasNoDuplicatesInBoxes(numbers, boardSize)
}

fun hasAllNumbersInRange(numbersSolution: List<Int>, boardSize: Int): Boolean {
    numbersSolution.forEach { if (it !in 1..boardSize) return false }
    return true
}

fun hasNoDuplicatesHorizontally(numbersSolution: List<Int>, boardSize: Int): Boolean {
    val numbersCurrentRow = mutableListOf<Int>()
    val firstIndexOfLastRow = boardSize * (boardSize - 1)
    for (row in 0..firstIndexOfLastRow step boardSize) {
        numbersCurrentRow.clear()
        for (currentIndex in row until row + boardSize) {
            val currentNumber = numbersSolution[currentIndex]
            if (numbersCurrentRow.contains(currentNumber)) return false
            else numbersCurrentRow.add(currentNumber)
        }
    }
    return true
}

fun hasNoDuplicatesVertically(numbersSolution: List<Int>, boardSize: Int): Boolean {
    val numbersCurrentColumn = mutableListOf<Int>()
    for (column in 0 until boardSize) {
        numbersCurrentColumn.clear()
        val lastIndexInColumn = column + boardSize * (boardSize - 1)
        for (index in column..lastIndexInColumn step boardSize) {
            val currentNumber = numbersSolution[index]
            if (numbersCurrentColumn.contains(currentNumber)) return false
            else numbersCurrentColumn.add(currentNumber)
        }
    }
    return true
}

fun hasNoDuplicatesInBoxes(numbersSolution: List<Int>, boardSize: Int): Boolean {
    val numbersCurrentBox = mutableListOf<Int>()
    val boxSize = boardSize.sqrt()
    val lastBoxFirstIndex = boardSize * (boardSize - boxSize) + boardSize - boxSize
    var currentBoxFirstIndex = 0
    var iterationCounter = 0
    while (currentBoxFirstIndex <= lastBoxFirstIndex) {
        numbersCurrentBox.clear()
        for (columnFirstIndex in currentBoxFirstIndex until currentBoxFirstIndex + boxSize) {
            val columnLastIndex = columnFirstIndex + boardSize * (boxSize - 1)
            for (currentIndex in columnFirstIndex..columnLastIndex step boardSize) {
                val currentNumber = numbersSolution[currentIndex]
                if (numbersCurrentBox.contains(currentNumber)) return false
                else numbersCurrentBox.add(currentNumber)
            }
        }
        val nextStep = when (++iterationCounter % boxSize) {
            0 -> boardSize * (boxSize - 1) + boxSize
            else -> boxSize
        }
        currentBoxFirstIndex += nextStep
    }
    return true
}