package io.github.pawgli.sudoku.ui

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import io.github.pawgli.sudoku.utils.isPerfectSquare
import io.github.pawgli.sudoku.utils.sqrt
import timber.log.Timber
import java.lang.IllegalArgumentException
import java.util.*
import kotlin.math.min

private const val NONE_SELECTED = -1
private const val EMPTY_CELL_DEFAULT = 0

private const val BOARD_SIZE_DEFAULT = 9
private const val BORDER_WIDTH_MIN = 2f
private const val BORDER_WIDTH_MAX = 8f

private const val INITIAL_ALPHA = 255
private const val ADDED_ALPHA = 140
private const val SELECTED_CELL_ALPHA = 140
private const val HIGHLIGHTED_CELL_ALPHA = 70

class SudokuBoardView(context: Context, attrs: AttributeSet) : View(context, attrs) {

    private var onCellClickedListener: ((row: Int, column: Int) -> Unit)? = null

    /**
     * Number of cells in each line
     * The number has to be a perfect square, max value = 36
     */
    var boardSize: Int = BOARD_SIZE_DEFAULT
        set(value) {
            if (value.isPerfectSquare() && value <= 36) {
                field = value
                singleBoxSize = value.sqrt()
                numberOfCells = boardSize * boardSize
            } else if (!value.isPerfectSquare()) {
                throw IllegalArgumentException("Size of the board needs to be a perfect square")
            } else if (value > 36) {
                throw IllegalArgumentException("Size of the board cannot exceed 36")
            }
        }
    private var singleBoxSize: Int = boardSize.sqrt()
    private var numberOfCells: Int = boardSize * boardSize
    private var cellSizePx = 0f
    private var noteSizePx = 0f
    private var canvas = Canvas()

    /**
     * Width of lines surrounding the board, and separating single boxes
     * min value = 2, max value = 8
     */
    var borderLineWidthPx = BORDER_WIDTH_MAX
        set(value) {
            if (value < BORDER_WIDTH_MIN) field = BORDER_WIDTH_MIN
            if (value > BORDER_WIDTH_MAX) field = BORDER_WIDTH_MAX
            internalLineWidthPx = value / 2
            borderLinePaint.strokeWidth = borderLineWidthPx
            internalLinePaint.strokeWidth = internalLineWidthPx
        }
    private var internalLineWidthPx = borderLineWidthPx / 2

    var emptyCellValue = EMPTY_CELL_DEFAULT
    private var selectedRow = NONE_SELECTED
    private var selectedColumn = NONE_SELECTED

    private val initialIndexes = mutableListOf<Int>()
    private val highlightedNumbersIndexes = mutableListOf<Int>()
    private val numbers = mutableListOf<Int>()
    private val notes = mutableMapOf<Int, Set<Int>>()

    var selectedCellColor =  Color.DKGRAY
    var highlightedCellColor = Color.GRAY
    var gridColor = Color.BLACK
    var numberColor = Color.BLACK
    var highlightedNumberColor = Color.RED

    private val cellPaint = Paint().apply {
        style = Paint.Style.FILL
        alpha = HIGHLIGHTED_CELL_ALPHA
    }

    private val borderLinePaint = Paint().apply {
        style = Paint.Style.STROKE
        strokeWidth = borderLineWidthPx
    }

    private val internalLinePaint = Paint().apply {
        style = Paint.Style.STROKE
        strokeWidth = borderLineWidthPx / 2
    }

    private val numberPaint = Paint().apply {
        style = Paint.Style.FILL_AND_STROKE
        textAlign = Paint.Align.CENTER
        typeface = Typeface.SANS_SERIF
        isAntiAlias = true
    }

    private val notesPaint = Paint().apply {
        style = Paint.Style.FILL_AND_STROKE
        textAlign = Paint.Align.CENTER
        typeface = Typeface.SANS_SERIF
        isAntiAlias = true
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val measureSpec = min(widthMeasureSpec, heightMeasureSpec)
        super.onMeasure(measureSpec, measureSpec)
    }

    override fun onDraw(canvas: Canvas) {
        this.canvas = canvas
        updateVariables()
        fillCells()
        drawNumbers()
        drawNotes()
        drawBoard()
    }

    private fun updateVariables() {
        cellSizePx = (width - (2 * borderLineWidthPx)) / boardSize
        noteSizePx = cellSizePx / singleBoxSize
        numberPaint.textSize = cellSizePx * .7f
        notesPaint.textSize = cellSizePx / singleBoxSize
    }

    private fun fillCells() {
        if (selectedColumn == NONE_SELECTED || selectedRow == NONE_SELECTED) return
        for (row in 0..boardSize) {
            for (column in 0..boardSize) {

                val isSelectedCell = row == selectedRow && column == selectedColumn
                val isSelectedLine = row == selectedRow || column == selectedColumn
                val isSelectedBox = row / singleBoxSize == selectedRow / singleBoxSize
                        && column / singleBoxSize == selectedColumn / singleBoxSize

                when {
                    isSelectedCell -> {
                        highlightCell(row, column, isSelected = true)
                    }
                    isSelectedLine -> {
                        highlightCell(row, column, isSelected = false)
                    }
                    isSelectedBox -> {
                        highlightCell(row, column, isSelected = false)
                    }
                }
            }
        }
    }

    private fun highlightCell(row: Int, column: Int, isSelected: Boolean) {
        cellPaint.color = if (isSelected) selectedCellColor else highlightedCellColor
        cellPaint.alpha = if (isSelected) SELECTED_CELL_ALPHA else HIGHLIGHTED_CELL_ALPHA
        canvas.drawRect(
            column * cellSizePx + borderLineWidthPx,
            row * cellSizePx + borderLineWidthPx,
            (column + 1) * cellSizePx + borderLineWidthPx,
            (row + 1) * cellSizePx + borderLineWidthPx,
            cellPaint)
    }

    private fun drawNumbers() {
        for ((index, number) in numbers.withIndex()) {
            if (number != emptyCellValue) {
                updateNumberPaint(index)
                drawNumber(index)
            }
        }
    }

    private fun updateNumberPaint(numberIndex: Int) {
        numberPaint.color =
            if (highlightedNumbersIndexes.contains(numberIndex)) highlightedNumberColor
            else numberColor
        numberPaint.alpha =
            if (initialIndexes.contains(numberIndex)) INITIAL_ALPHA
            else ADDED_ALPHA
    }

    private fun drawNumber(index: Int) {
        val numberString = numbers[index].toString()
        val bounds = Rect()
        numberPaint.getTextBounds(numberString, 0, numberString.length, bounds)
        canvas.drawText(
            numberString,
            getNumberPositionX(index),
            getNumberPositionY(index, bounds.height()),
            numberPaint)
    }

    private fun getNumberPositionX(index: Int): Float {
        return (getColumn(index) * cellSizePx + borderLineWidthPx) + cellSizePx / 2
    }

    private fun getNumberPositionY(index: Int, numberHeight: Int): Float {
        return (getRow(index) * cellSizePx + borderLineWidthPx) + cellSizePx / 2 + numberHeight / 2
    }

    private fun getColumn(cellIndex: Int) = cellIndex % boardSize

    private fun getRow(cellIndex: Int) = cellIndex / boardSize

    private fun drawNotes() {
        notesPaint.color = numberColor
        for((index, notesSet) in notes) {
            notesSet.forEach { drawNote(index, it) }
        }
    }

    private fun drawNote(index: Int, note: Int) {
        val noteString = note.toString()
        val bounds = Rect()
        notesPaint.getTextBounds(noteString, 0, noteString.length, bounds)
        canvas.drawText(
            noteString,
            getNotePositionX(index, note),
            getNotePositionY(index, note, bounds.height()),
            notesPaint)
    }

    private fun getNotePositionX(index: Int, note: Int): Float {
        val columnInCell = (note - 1) % singleBoxSize
        return (getColumn(index) * cellSizePx
                + borderLineWidthPx
                + columnInCell * noteSizePx
                + noteSizePx / 2)
    }

    private fun getNotePositionY(index: Int, note: Int, noteHeight: Int): Float {
        val rowInCell = (note - 1) / singleBoxSize
        return (getRow(index) * cellSizePx
                + borderLineWidthPx
                + rowInCell * noteSizePx
                + noteSizePx / 2
                + noteHeight / 2)
    }

    private fun drawBoard() {
        drawBorder()
        drawInternalLines()
    }

    private fun drawBorder() {
        borderLinePaint.color = gridColor
        val borderWidthCorrection = borderLineWidthPx / 2
        canvas.drawRect(
            0f + borderWidthCorrection,
            0f + borderWidthCorrection,
            width.toFloat() - borderWidthCorrection,
            height.toFloat() - borderWidthCorrection,
            borderLinePaint)
    }

    private fun drawInternalLines() {
        for (i in 1 until boardSize) {
            val paint = when (i % singleBoxSize) {
                0 -> borderLinePaint
                else -> internalLinePaint
            }
            paint.color = gridColor
            val currentPos = i * cellSizePx + borderLineWidthPx
            canvas.drawLine(currentPos, 0f, currentPos, height.toFloat(), paint)
            canvas.drawLine(0f, currentPos, width.toFloat(), currentPos, paint)
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        return when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                onBoardClicked(event.x, event.y)
                return true
            }
            else -> false
        }
    }

    private fun onBoardClicked(positionX: Float, positionY: Float) {
        val clickedRow = (positionY / cellSizePx).toInt()
        val clickedColumn = (positionX / cellSizePx).toInt()
        if (isCellInBounds(clickedRow, clickedColumn)) {
            notifyOnCellSelectedListener(clickedRow, clickedColumn)
        }
    }

    private fun isCellInBounds(row: Int, column: Int): Boolean {
        return row in 0 until  boardSize && column in 0 until  boardSize
    }

    fun setOnCellClickedListener(listener: (row: Int, column: Int) -> Unit) {
        onCellClickedListener = listener
    }

    private fun notifyOnCellSelectedListener(row: Int, column: Int) {
        onCellClickedListener?.invoke(row, column)
    }

    fun setSelectedCell(row: Int, column: Int) {
        selectedRow = row
        selectedColumn = column
        invalidate()
    }

    fun setInitialIndexes(indexes: List<Int>) {
        initialIndexes.clear()
        initialIndexes.addAll(indexes)
        invalidate()
    }

    fun setHighlightedNumbersIndexes(indexes: List<Int>) {
        highlightedNumbersIndexes.clear()
        highlightedNumbersIndexes.addAll(indexes)
        invalidate()
    }

    fun setNumbers(numbers: List<Int>) {
        if (numbers.size == numberOfCells) {
            this.numbers.clear()
            this.numbers.addAll(numbers)
            invalidate()
        } else {
            throw IllegalArgumentException("Size of the list must be equal to the number of cells")
        }
    }

    fun setNotes(notes: Map<Int, Set<Int>>) {
        this.notes.clear()
        this.notes.putAll(notes)
        invalidate()
    }
}