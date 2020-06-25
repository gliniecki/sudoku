package io.github.pawgli.sudoku.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import kotlin.math.min

private const val NONE_SELECTED = -1
private const val SINGLE_BOX_SIZE = 3
private const val BOARD_SIZE = 9

class StandardSudokuBoardView(context: Context, attrs: AttributeSet) : View(context, attrs) {

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
        color = Color.LTGRAY
    }

    private val activeCellPaint = Paint().apply {
        style = Paint.Style.FILL
        color = Color.YELLOW
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val measureSpec = min(widthMeasureSpec, heightMeasureSpec)
        super.onMeasure(measureSpec, measureSpec)
    }

    override fun onDraw(canvas: Canvas) {
        cellSizePx = (width / BOARD_SIZE).toFloat()
        fillCells(canvas)
        drawBoard(canvas)
    }

    private fun fillCells(canvas: Canvas) {
        if (selectedColumn == NONE_SELECTED || selectedRow == NONE_SELECTED) return
        for (row in 0..BOARD_SIZE) {
            for (column in 0..BOARD_SIZE) {

                val isSelectedCell = row == selectedRow && column == selectedColumn
                val isSelectedLine = row == selectedRow || column == selectedColumn
                val isSelectedBox = row / SINGLE_BOX_SIZE == selectedRow / SINGLE_BOX_SIZE
                        && column / SINGLE_BOX_SIZE == selectedColumn / SINGLE_BOX_SIZE

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
        for (i in 1 until BOARD_SIZE) {
            val paint = when (i % SINGLE_BOX_SIZE) {
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
        selectedRow = (positionY / cellSizePx).toInt()
        selectedColumn = (positionX / cellSizePx).toInt()
        invalidate()
    }
}