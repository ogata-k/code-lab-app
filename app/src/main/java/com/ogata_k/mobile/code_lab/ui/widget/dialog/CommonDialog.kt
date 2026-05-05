package com.ogata_k.mobile.code_lab.ui.widget.dialog

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.ogata_k.mobile.code_lab.R

sealed interface CommonDialogTitle {
    @Composable
    fun asString(): String {
        // TODO 本当に使うものに置き換える
        return "$this"
    }
}

sealed interface CommonDialogMessage {
    @Composable
    fun asString(): String {
        // TODO 本当に使うものに置き換える
        return "$this"
    }

    // TODO 本当に使うものに置き換える
    data object Initialized : CommonDialogMessage
}

sealed interface CommonDialogButtonText {
    @Composable
    fun asString(): String {
        // TODO 本当に使うものに置き換える
        return "$this"
    }
}

/**
 * 画面共通で使われるダイアログのデータ
 */
sealed interface CommonDialogData {
    data class ShowErrorDialog(
        val message: CommonDialogMessage,
        val onDismiss: (() -> Unit)?,
    ) : CommonDialogData

    data class ShowConfirmDialog(
        val message: CommonDialogMessage,
        val onDismiss: (() -> Unit)?,
    ) : CommonDialogData

    data class ShowRequestActionDialog(
        val title: CommonDialogTitle,
        val message: CommonDialogMessage,
        val cancelButtonText: CommonDialogButtonText? = null,
        val onDismiss: (() -> Unit)?,
        val actionButtonText: CommonDialogButtonText,
        val onAction: (showingDialog: ShowRequestActionDialog) -> Unit,
    ) : CommonDialogData
}

@Composable
fun CommonDialog(
    effect: CommonDialogData,
    onDismiss: () -> Unit,
) {
    when (effect) {
        is CommonDialogData.ShowErrorDialog -> {
            BasicDialog(
                title = stringResource(R.string.dialog_title_error),
                text = effect.message.asString(),
                dismissButtonText = stringResource(R.string.btn_close),
                onDismissRequest = {
                    onDismiss()
                    effect.onDismiss?.invoke()
                },
            )
        }

        is CommonDialogData.ShowConfirmDialog -> {
            BasicDialog(
                title = stringResource(R.string.dialog_title_confirm),
                text = effect.message.asString(),
                dismissButtonText = stringResource(R.string.btn_confirm),
                onDismissRequest = {
                    onDismiss()
                    effect.onDismiss?.invoke()
                },
            )
        }

        is CommonDialogData.ShowRequestActionDialog -> {
            BasicDialog(
                title = effect.title.asString(),
                text = effect.message.asString(),
                dismissButtonText = if (effect.cancelButtonText != null) {
                    effect.cancelButtonText.asString()
                } else {
                    stringResource(R.string.btn_cancel)
                },
                onDismissRequest = {
                    onDismiss()
                    effect.onDismiss?.invoke()
                },
                action = Pair(
                    effect.actionButtonText.asString(),
                    {
                        onDismiss()
                        effect.onAction(effect)
                    }
                )
            )
        }
    }
}