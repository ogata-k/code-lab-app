package com.ogata_k.mobile.code_lab.ui.widget.snackbar

import android.content.Context
import com.ogata_k.mobile.code_lab.R
import com.ogata_k.mobile.code_lab.ui.widget.UiCallback

sealed interface CommonSnackbarMessage {
    fun asString(context: Context): String {
        val resId = when (this) {
            Initialized -> R.string.snackbar_initialized
            ValueOutOfRange -> R.string.snackbar_value_out_of_range
            InvalidState -> R.string.snackbar_invalid_state
        }

        return context.getString(resId)
    }

    data object Initialized : CommonSnackbarMessage

    data object ValueOutOfRange : CommonSnackbarMessage

    data object InvalidState : CommonSnackbarMessage
}

sealed interface CommonSnackbarLabel {
    fun asString(context: Context): String {
        // TODO: 他に合わせて修正
        return this.toString()
    }
}

/**
 * 画面共通で使われるスナックバーのデータ
 */
data class CommonSnackbarData(
    val message: CommonSnackbarMessage,
    val actionLabel: CommonSnackbarLabel? = null,
    val onDismiss: UiCallback? = null,
    val onAction: UiCallback? = null,
)
