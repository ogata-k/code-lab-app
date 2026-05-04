package com.ogata_k.mobile.code_lab.ui.widget.snackbar

import android.content.Context

sealed interface CommonSnackbarMessage {
    fun asString(context: Context): String {
        // TODO 本当に使うものに置き換える
        return "$this"
    }

    // TODO 本当に使うものに置き換える
    data object Initialized : CommonSnackbarMessage
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
    val onAction: (() -> Unit)? = null
)
