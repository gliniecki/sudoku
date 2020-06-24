package io.github.pawgli.sudoku.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import timber.log.Timber
import kotlin.math.min

private const val NONE_SELECTED = -1

class StandardSudokuBoardView(context: Context, attrs: AttributeSet) : View(context, attrs) {

    private var singleBoxSize = 3
    private var boardSize = 9

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
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

//        val widthSpecMode = MeasureSpec.getMode(widthMeasureSpec)
//        val heightSpecMode = MeasureSpec.getMode(heightMeasureSpec)
//
//        when (widthSpecMode) {
//            MeasureSpec.EXACTLY -> Timber.d( "[onMeasure] widthSpecMode: EXACTLY")
//            MeasureSpec.UNSPECIFIED -> Timber.d("[onMeasure] widthSpecMode: UNSPECIFIED")
//            MeasureSpec.AT_MOST -> Timber.d("[onMeasure] widthSpecMode: AT_MOST")
//        }
//
//
//        when (heightSpecMode) {
//            MeasureSpec.EXACTLY -> Timber.d("S[onMeasure] heightSpecMode: EXACTLY")
//            MeasureSpec.UNSPECIFIED -> Timber.d("[onMeasure] heightSpecMode: UNSPECIFIED")
//            MeasureSpec.AT_MOST -> Timber.d("[onMeasure] heightSpecMode: AT_MOST")
//        }

        val size = min(widthMeasureSpec, heightMeasureSpec)
        setMeasuredDimension(size, size)
    }

    override fun onDraw(canvas: Canvas) {
//        Timber.d("[onDraw] width = $width; height = $height")
        cellSizePx = (width / boardSize).toFloat()
        fillCells(canvas)
        drawBoard(canvas)
    }

    private fun fillCells(canvas: Canvas) {
        if (selectedColumn == NONE_SELECTED || selectedRow == NONE_SELECTED) return
        for (row in 0..boardSize) {
            for (column in 0..boardSize) {
                if (row == selectedRow && column == selectedColumn) {
                    fillCell(canvas, row, column, activeCellPaint)
                } else if (row == selectedRow || column == selectedColumn) {
                    fillCell(canvas, row, column, highlightedCellPaint)
                } else if (row / singleBoxSize == selectedRow / singleBoxSize && column / singleBoxSize == selectedColumn / singleBoxSize) {
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
        for (i in 1 until boardSize) {
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
        selectedRow = (positionY / cellSizePx).toInt()
        selectedColumn = (positionX / cellSizePx).toInt()
        invalidate()
    }
}