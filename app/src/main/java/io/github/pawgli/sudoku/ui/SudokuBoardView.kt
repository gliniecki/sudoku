package io.github.pawgli.sudoku.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import java.lang.IllegalArgumentException
import kotlin.math.floor
import kotlin.math.min
import kotlin.math.sqrt

private const val NONE_SELECTED = -1
private const val DEFAULT_BOARD_SIZE = 9

class SudokuBoardView(context: Context, attrs: AttributeSet) : View(context, attrs) {

    private var onCellClickedListener: ((row: Int, column: Int) -> Unit)? = null

    var boardSize: Int = DEFAULT_BOARD_SIZE
        set(value) {
            if (value.isPerfectSquare()) {
                field = value
                singleBoxSize = value.sqrt()
            } else {
                throw IllegalArgumentException("Size of the board needs to be a perfect square")
            }
        }
    private var singleBoxSize: Int = boardSize.sqrt()

    private var cellSizePx = 0f

    private var selectedRow = NONE_SELECTED
    private var selectedColumn = NONE_SELECTED

    private val thickLinePaint = Paint().apply {
        style = Paint.Style.STROKE
        color = Color.BLACK
        strokeWidth = 8f
    }

    private val thinLinePaint = Paint().apply {
        style = Paint.Style.STROKE
        color = Color.BLACK
        strokeWidth = 4f
    }

    private val highlightedCellPaint = Paint().apply {
        style = Paint.Style.FILL
        color = Color.parseColor("#CBFDC8")
    }

    private val activeCellPaint = Paint().apply {
        style = Paint.Style.FILL
        color = Color.parseColor("#FBFBCA")
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val measureSpec = min(widthMeasureSpec, heightMeasureSpec)
        super.onMeasure(measureSpec, measureSpec)
    }

    override fun onDraw(canvas: Canvas) {
        cellSizePx = (width / boardSize).toFloat()
        fillCells(canvas)
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
            column * cellSizePx,
            row * cellSizePx,
            (column + 1) * cellSizePx,
            (row + 1) * cellSizePx,
            paint)
    }

    private fun drawBoard(canvas: Canvas) {
        drawBorder(canvas)
        drawInternalLines(canvas)
    }

    private fun drawBorder(canvas: Canvas) {
        val paintThicknessCorrection = thickLinePaint.strokeWidth / 2f
        canvas.drawRect(
            0f + paintThicknessCorrection,
            0f + paintThicknessCorrection,
            width.toFloat() - paintThicknessCorrection,
            height.toFloat() - paintThicknessCorrection,
            thickLinePaint)
    }

    private fun drawInternalLines(canvas: Canvas) {
        for (i in 1 until DEFAULT_BOARD_SIZE) {
            val paint = when (i % singleBoxSize) {
                0 -> thickLinePaint
                else -> thinLinePaint
            }
            val currentPos = i * cellSizePx
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
        notifyOnCellSelectedListener(clickedRow, clickedColumn)
    }

    fun setOnCellClickedListener(listener: (row: Int, column: Int) -> Unit) {
        onCellClickedListener = listener
    }

    private fun notifyOnCellSelectedListener(row: Int, column: Int) {
        onCellClickedListener?.invoke(row, column)
    }

    fun updateSelectedCell(row: Int, column: Int) {
        selectedRow = row
        selectedColumn = column
        invalidate()
    }
}

fun Int.sqrt(): Int { return sqrt(this.toDouble()).toInt() }

fun Int.isPerfectSquare(): Boolean {
    val sqrt = sqrt(this.toDouble())
    val sqrtFloor = floor(sqrt)
    return sqrt == sqrtFloor
}