package com.ogata_k.mobile.code_lab.common.global_ui

/**
 * アプリ全体で共通のUiEffect
 */
sealed interface GlobalUiEffect {

    data class ShowSnackbar(
        val message: String,
        val actionLabel: String? = null,
        val onAction: (() -> Unit)? = null
    ) : GlobalUiEffect

    data class ShowToast(
        val message: String,
        val showLong: Boolean = false,
    ) : GlobalUiEffect

    data class ShowErrorDialog(
        val message: String,
        val onDismiss: (() -> Unit),
    ) : GlobalUiEffect

    data class ShowConfirmDialog(
        val message: String,
        val onDismiss: () -> Unit,
    ) : GlobalUiEffect

    data class ShowRequestActionDialog(
        val title: String,
        val message: String,
        val cancelButtonText: String? = null,
        val onDismiss: () -> Unit,
        val actionButtonText: String,
        val onAction: () -> Unit,
    ) : GlobalUiEffect
}