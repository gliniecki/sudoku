package io.github.pawgli.sudoku.ui

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import java.lang.IllegalArgumentException
import kotlin.math.floor
import kotlin.math.min
import kotlin.math.sqrt

private const val NONE_SELECTED = -1
private const val EMPTY_CELL_DEFAULT = 0
private const val BOARD_SIZE_DEFAULT = 9
private const val BORDER_WIDTH_MIN = 2f
private const val BORDER_WIDTH_MAX = 8f

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

    /**
     * Width of lines surrounding the board, and separating single boxes
     * min value = 2, max value = 8
     */
    var borderLineWidthPx = BORDER_WIDTH_MAX
        set(value) {
            if (value < BORDER_WIDTH_MIN) field = BORDER_WIDTH_MIN
            if (value > BORDER_WIDTH_MAX) field = BORDER_WIDTH_MAX
            internalLineWidthPx = value / 2
        }
    private var internalLineWidthPx = borderLineWidthPx / 2

    var emptyCellValue = EMPTY_CELL_DEFAULT
    private var selectedRow = NONE_SELECTED
    private var selectedColumn = NONE_SELECTED

    private val initialIndexes = mutableListOf<Int>()
    private val numbers = mutableListOf<Int>()
    private val notes = mutableListOf<MutableSet<Int>>()

    private val borderLinePaint = Paint().apply {
        style = Paint.Style.STROKE
        color = Color.BLACK
        strokeWidth = borderLineWidthPx
    }

    private val internalLinePaint = Paint().apply {
        style = Paint.Style.STROKE
        color = Color.BLACK
        strokeWidth = borderLineWidthPx / 2
    }

    private val highlightedCellPaint = Paint().apply {
        style = Paint.Style.FILL
        color = Color.parseColor("#CBFDC8")
    }

    private val activeCellPaint = Paint().apply {
        style = Paint.Style.FILL
        color = Color.parseColor("#FBFBCA")
    }

    private val initialNumberPaint = Paint(). apply {
        style = Paint.Style.FILL_AND_STROKE
        color = Color.BLACK
        textSize = 15 * resources.displayMetrics.density
        textAlign = Paint.Align.CENTER
        typeface = Typeface.SANS_SERIF
        isAntiAlias = true
    }

    private val addedNumberPaint = Paint(). apply {
        style = Paint.Style.FILL_AND_STROKE
        color = Color.GRAY
        textSize = 15 * resources.displayMetrics.density
        textAlign = Paint.Align.CENTER
        typeface = Typeface.SANS_SERIF
        isAntiAlias = true
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val measureSpec = min(widthMeasureSpec, heightMeasureSpec)
        super.onMeasure(measureSpec, measureSpec)
    }

    override fun onDraw(canvas: Canvas) {
        cellSizePx = (width - (2 * borderLineWidthPx)) / boardSize
        initialNumberPaint.textSize = cellSizePx * .7f
        addedNumberPaint.textSize = cellSizePx * .7f
        fillCells(canvas)
        drawNumbers(canvas)
        drawBoard(canvas)
    }

    private fun fillCells(canvas: Canvas) {
        if (selectedColumn == NONE_SELECTED || selectedRow == NONE_SELECTED) return
        for (row in 0..boardSize) {
            for (column in 0..boardSize) {

                val isSelectedCell = row == selectedRow && column == selectedColumn
                val isSelectedLine = row == selectedRow || column == selectedColumn
                val isSelectedBox = row / singleBoxSize == selectedRow / singleBoxSize
                        && column / singleBoxSize == selectedColumn / singleBoxSize

                if (isSelectedCell) {
                    fillCell(canvas, row, column, activeCellPaint)
                } else if (isSelectedLine) {
                    fillCell(canvas, row, column, highlightedCellPaint)
                } else if (isSelectedBox) {
                    fillCell(canvas, row, column, highlightedCellPaint)
                }
            }
        }
    }

    private fun fillCell(canvas: Canvas, row: Int, column: Int, paint: Paint) {
        canvas.drawRect(
            column * cellSizePx + borderLineWidthPx,
            row * cellSizePx + borderLineWidthPx,
            (column + 1) * cellSizePx + borderLineWidthPx,
            (row + 1) * cellSizePx + borderLineWidthPx,
            paint)
    }

    private fun drawNumbers(canvas: Canvas) {
        for ((index, number) in numbers.withIndex()) {
            if (number != emptyCellValue) {
                val paint = if (initialIndexes.contains(index)) initialNumberPaint else addedNumberPaint
                val numberString = numbers[index].toString()
                val bounds = Rect()
                paint.getTextBounds(numberString, 0, numberString.length, bounds)
                val textHeight = bounds.height()
                val x = (getColumn(index) * cellSizePx + borderLineWidthPx) + cellSizePx / 2
                val y = (getRow(index) * cellSizePx + borderLineWidthPx) + cellSizePx / 2 + textHeight / 2
                canvas.drawText(numberString, x, y, paint)
            }
        }
    }

    private fun getRow(cellIndex: Int) = cellIndex / boardSize

    private fun getColumn(cellIndex: Int) = cellIndex % boardSize

    private fun drawNotes(canvas: Canvas) {

    }

    private fun drawBoard(canvas: Canvas) {
        drawBorder(canvas)
        drawInternalLines(canvas)
    }

    private fun drawBorder(canvas: Canvas) {
        val borderWidthCorrection = borderLineWidthPx / 2
        canvas.drawRect(
            0f + borderWidthCorrection,
            0f + borderWidthCorrection,
            width.toFloat() - borderWidthCorrection,
            height.toFloat() - borderWidthCorrection,
            borderLinePaint)
    }

    private fun drawInternalLines(canvas: Canvas) {
        for (i in 1 until boardSize) {
            val paint = when (i % singleBoxSize) {
                0 -> borderLinePaint
                else -> internalLinePaint
            }
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
}

fun Int.sqrt(): Int { return sqrt(this.toDouble()).toInt() }

fun Int.isPerfectSquare(): Boolean {
    val sqrt = sqrt(this.toDouble())
    val sqrtFloor = floor(sqrt)
    return sqrt == sqrtFloor
}