package com.ogata_k.mobile.code_lab.feature.fifteen_puzzle_sample

import com.ogata_k.mobile.code_lab.core.mvi.Intent
import com.ogata_k.mobile.code_lab.domain.enum.FifteenPuzzleDifficulty
import com.ogata_k.mobile.code_lab.ui.widget.dialog.CommonDialogData

/**
 * FifteenPuzzleSample featureに対するユーザーの意図（操作）
 */
sealed interface FifteenPuzzleSampleIntent : Intent<FifteenPuzzleSampleAction> {
    data class DismissDialog(val data: CommonDialogData) : FifteenPuzzleSampleIntent

    data class ChangeGridSize(val gridSize: UInt) : FifteenPuzzleSampleIntent
    data class ChangeDifficulty(val difficulty: FifteenPuzzleDifficulty) : FifteenPuzzleSampleIntent
    data object ConfirmGameSettingBeforePlay : FifteenPuzzleSampleIntent
    data object StartPlayGame : FifteenPuzzleSampleIntent
    data object RetryPlayGameByInvalidDifficulty : FifteenPuzzleSampleIntent
    data class TapBoardCell(val cellValue: UInt) : FifteenPuzzleSampleIntent

    override fun toAction(): FifteenPuzzleSampleAction? = when (this) {
        is DismissDialog -> FifteenPuzzleSampleAction.DismissDialog(this.data)
        is ChangeGridSize -> FifteenPuzzleSampleAction.UpdateGridSizeSetting(this.gridSize)
        is ChangeDifficulty -> FifteenPuzzleSampleAction.UpdateDifficultySetting(this.difficulty)
        ConfirmGameSettingBeforePlay -> FifteenPuzzleSampleAction.ConfirmGameSettingBeforePlay
        StartPlayGame -> FifteenPuzzleSampleAction.StartPlayGame
        RetryPlayGameByInvalidDifficulty -> FifteenPuzzleSampleAction.RetryPlayGameByInvalidDifficulty
        is TapBoardCell -> FifteenPuzzleSampleAction.MoveCell(this.cellValue)
    }
}
