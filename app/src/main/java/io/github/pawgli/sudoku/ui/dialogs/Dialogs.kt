package io.github.pawgli.sudoku.ui.dialogs

import android.app.AlertDialog
import android.content.Context

fun getPosNegDialog(
    context: Context,
    title: String,
    message: String,
    onPositive: () -> Unit): AlertDialog {
    context.getAlertBuilder().run {
        setTitle(title)
        setMessage(message)
        setPositiveButton(android.R.string.yes) { _, _ -> onPositive.invoke() }
        setNegativeButton(android.R.string.no, null)
        setCancelable(true)
        return create()
    }
}