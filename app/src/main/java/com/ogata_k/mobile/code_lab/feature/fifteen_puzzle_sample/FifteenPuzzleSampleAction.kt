package com.ogata_k.mobile.code_lab.feature.fifteen_puzzle_sample

import com.ogata_k.mobile.code_lab.core.mvi.Action
import com.ogata_k.mobile.code_lab.core.mvi.DefaultExecutionKey
import com.ogata_k.mobile.code_lab.core.mvi.ExecutionStrategy
import com.ogata_k.mobile.code_lab.domain.enum.FifteenPuzzleDifficulty
import com.ogata_k.mobile.code_lab.ui.widget.dialog.CommonDialogData

/**
 * FifteenPuzzleSample featureの内部で処理されるアクション
 */
sealed interface FifteenPuzzleSampleAction : Action {
    data class DismissDialog(val data: CommonDialogData) : FifteenPuzzleSampleAction {
        override val strategy: ExecutionStrategy = ExecutionStrategy.Parallel
    }

    data class UpdateGridSizeSetting(val gridSize: UInt) : FifteenPuzzleSampleAction {
        override val strategy: ExecutionStrategy
            get() = ExecutionStrategy.Parallel
    }

    data class UpdateDifficultySetting(val difficulty: FifteenPuzzleDifficulty) :
        FifteenPuzzleSampleAction {
        override val strategy: ExecutionStrategy
            get() = ExecutionStrategy.Parallel
    }

    data object ConfirmGameSettingBeforePlay : FifteenPuzzleSampleAction {
        override val strategy: ExecutionStrategy
            get() = ExecutionStrategy.Sequential(DefaultExecutionKey)
    }

    data object StartPlayGame : FifteenPuzzleSampleAction {
        override val strategy: ExecutionStrategy
            get() = ExecutionStrategy.Sequential(DefaultExecutionKey)
    }

    data object CheckInitialBoardDifficulty : FifteenPuzzleSampleAction {
        override val strategy: ExecutionStrategy
            get() = ExecutionStrategy.Sequential(DefaultExecutionKey)
    }

    data object RetryPlayGameByInvalidDifficulty : FifteenPuzzleSampleAction {
        override val strategy: ExecutionStrategy
            get() = ExecutionStrategy.Sequential(DefaultExecutionKey)
    }

}