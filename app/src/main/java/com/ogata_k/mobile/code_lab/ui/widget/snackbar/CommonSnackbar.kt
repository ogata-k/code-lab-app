package com.ogata_k.mobile.code_lab.ui.widget.snackbar

import android.content.Context
import com.ogata_k.mobile.code_lab.R

sealed interface CommonSnackbarMessage {
    fun asString(context: Context): String {
        val resId = when (this) {
            Initialized -> R.string.snackbar_initialized
            ValueOutOfRange -> R.string.snackbar_value_out_of_range
        }

        return context.getString(resId)
    }

    // TODO 本当に使うものに置き換える
    data object Initialized : CommonSnackbarMessage

    data object ValueOutOfRange : CommonSnackbarMessage
}

sealed interface CommonSnackbarLabel {
    fun asString(context: Context): String {
        // TODO 本当に使うものに置き換える
        return "$this"
    }
}

/**
 * 画面共通で使われるスナックバーのデータ
 */
data class CommonSnackbarData(
    val message: CommonSnackbarMessage,
    val actionLabel: CommonSnackbarLabel? = null,
    val onDismiss: (() -> Unit)? = null,
    val onAction: (() -> Unit)? = null,
)
