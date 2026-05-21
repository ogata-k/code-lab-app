package com.ogata_k.mobile.code_lab.feature.fifteen_puzzle_sample

import com.ogata_k.mobile.code_lab.core.mvi.Action
import com.ogata_k.mobile.code_lab.core.mvi.ExecutionStrategy

/**
 * FifteenPuzzleSample featureの内部で処理されるアクション
 */
sealed interface FifteenPuzzleSampleAction : Action {
    // TODO: 本来のActionに書き換える
    data object Initialize : FifteenPuzzleSampleAction {
        override val strategy: ExecutionStrategy = ExecutionStrategy.Parallel
    }
}