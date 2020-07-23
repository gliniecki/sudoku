package io.github.pawgli.sudoku.ui.dialogs

import android.app.AlertDialog
import android.content.Context
import android.os.Build
import io.github.pawgli.sudoku.R

@Suppress("DEPRECATION")
fun Context.getAlertBuilder(): AlertDialog.Builder {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
        AlertDialog.Builder(this, R.style.DialogTheme)
    } else {
        AlertDialog.Builder(this, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT)
    }
}