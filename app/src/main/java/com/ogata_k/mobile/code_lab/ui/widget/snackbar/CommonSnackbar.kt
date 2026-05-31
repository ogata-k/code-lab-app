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
            DifficultyMismatch -> R.string.snackbar_difficulty_mismatch
        }

        return context.getString(resId)
    }

    data object Initialized : CommonSnackbarMessage

    data object ValueOutOfRange : CommonSnackbarMessage

    data object InvalidState : CommonSnackbarMessage

    data object DifficultyMismatch : CommonSnackbarMessage
}

sealed interface CommonSnackbarLabel {
    fun asString(context: Context): String {
        val resId = when (this) {
            Retry -> R.string.snackbar_label_retry
        }

        return context.getString(resId)
    }

    data object Retry : CommonSnackbarLabel
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
