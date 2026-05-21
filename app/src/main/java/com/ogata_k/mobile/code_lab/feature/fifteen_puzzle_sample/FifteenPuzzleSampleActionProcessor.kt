package com.ogata_k.mobile.code_lab.feature.fifteen_puzzle_sample

import com.ogata_k.mobile.code_lab.core.mvi.ActionProcessor
import com.ogata_k.mobile.code_lab.core.mvi.StoreScope
import javax.inject.Inject

/**
 * FifteenPuzzleSample featureのアクションを処理し、ミューテーションを生成するクラス
 */
class FifteenPuzzleSampleActionProcessor @Inject constructor() :
    ActionProcessor<FifteenPuzzleSampleUiState, FifteenPuzzleSampleUiEffect, FifteenPuzzleSampleIntent, FifteenPuzzleSampleAction, FifteenPuzzleSampleMutation> {
    override suspend fun process(
        action: FifteenPuzzleSampleAction,
        scope: StoreScope<FifteenPuzzleSampleUiState, FifteenPuzzleSampleUiEffect, FifteenPuzzleSampleIntent, FifteenPuzzleSampleAction, FifteenPuzzleSampleMutation>
    ) {
        when (action) {
            is FifteenPuzzleSampleAction.Initialize -> {
                // TODO: 実際の初期化処理
                scope.emitMutation(FifteenPuzzleSampleMutation.ToInitialized)
            }
        }
    }
}