package com.ogata_k.mobile.code_lab.global

/**
 * アプリ全体で共通のUiEffectで利用するメッセージ
 */
sealed interface GlobalUiEffectMessage {
    data object UnexpectedError : GlobalUiEffectMessage
}

/**
 * アプリ全体で共通のUiEffectで利用するメッセージ
 */
sealed interface GlobalUiEffectLabel {
}

/**
 * アプリ全体で共通のUiEffect
 */
sealed interface GlobalUiEffect {
    data class ShowToast(
        val message: GlobalUiEffectMessage,
        val showAsLong: Boolean = true,
    ) : GlobalUiEffect

    data class ShowCriticalAlertDialog(
        val message: GlobalUiEffectMessage,
        val onDismiss: (() -> Unit)? = null,
    ) : GlobalUiEffect

    data class ShowCriticalAlertSnackbar(
        val message: GlobalUiEffectMessage,
        val actionLabel: GlobalUiEffectLabel? = null,
        val onAction: (() -> Unit)? = null
    ) : GlobalUiEffect
}
