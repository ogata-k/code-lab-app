package com.ogata_k.mobile.code_lab.ui.widget.dialog

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.ogata_k.mobile.code_lab.R
import com.ogata_k.mobile.code_lab.common.logI
import com.ogata_k.mobile.code_lab.domain.enum.FifteenPuzzleDifficulty
import com.ogata_k.mobile.code_lab.ui.widget.UiCallback

sealed interface CommonDialogTitle {
    @Composable
    fun asString(): String {
        return when (this) {
            Confirm -> stringResource(R.string.dialog_title_confirm)
        }
    }

    data object Confirm : CommonDialogTitle
}

sealed interface CommonDialogMessage {
    @Composable
    fun asString(): String {
        return when (this) {
            is ConfirmStartFifteenPuzzleGame -> stringResource(R.string.dialog_content_confirm_fifteen_difficulty_before_play).format(
                this.gridSize,
                this.difficulty,
                this.estimateMaxScore
            )
        }
    }

    data class ConfirmStartFifteenPuzzleGame(
        val gridSize: UInt,
        val difficulty: FifteenPuzzleDifficulty,
        val estimateMaxScore: UInt
    ) : CommonDialogMessage
}

sealed interface CommonDialogButtonText {
    @Composable
    fun asString(): String {
        return when (this) {
            Cancel -> stringResource(R.string.btn_cancel)
            Confirm -> stringResource(R.string.btn_confirm)
            Start -> stringResource(R.string.btn_start)
        }
    }

    data object Confirm : CommonDialogButtonText
    data object Cancel : CommonDialogButtonText
    data object Start : CommonDialogButtonText
}

/**
 * 画面共通で使われるダイアログのデータ
 */
sealed interface CommonDialogData {
    data class ShowErrorDialog(
        val message: CommonDialogMessage,
        val onDismiss: UiCallback = UiCallback({
            logI("CommonMutation") { "error dialog on dismiss" }
        }),
    ) : CommonDialogData

    data class ShowConfirmDialog(
        val message: CommonDialogMessage,
        val onDismiss: UiCallback = UiCallback({
            logI("CommonMutation") { "confirm dialog on dismiss" }
        }),
    ) : CommonDialogData

    data class ShowRequestActionDialog(
        val title: CommonDialogTitle,
        val message: CommonDialogMessage,
        val cancelButtonText: CommonDialogButtonText = CommonDialogButtonText.Cancel,
        val onDismiss: UiCallback = UiCallback({
            logI("CommonMutation") { "request action dialog on dismiss" }
        }),
        val actionButtonText: CommonDialogButtonText,
        val onAction: (showingDialog: ShowRequestActionDialog) -> Unit,
    ) : CommonDialogData

    data class ShowLoading(
        val message: CommonDialogMessage? = null,
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
                    effect.onDismiss.invoke()
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
                    effect.onDismiss.invoke()
                },
            )
        }

        is CommonDialogData.ShowRequestActionDialog -> {
            BasicDialog(
                title = effect.title.asString(),
                text = effect.message.asString(),
                dismissButtonText = effect.cancelButtonText.asString(),
                onDismissRequest = {
                    onDismiss()
                    effect.onDismiss.invoke()
                },
                action = Pair(
                    effect.actionButtonText.asString(),
                    {
                        effect.onAction(effect)
                    }
                )
            )
        }

        is CommonDialogData.ShowLoading -> {
            LoadingDialog(
                message = effect.message?.asString()
            )
        }
    }
}
