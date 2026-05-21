package com.ogata_k.mobile.code_lab.feature.fifteen_puzzle_sample

import com.ogata_k.mobile.code_lab.core.mvi.Reducer

/**
 * FifteenPuzzleSample featureの現在の状態とミューテーションから新しい状態を生成するクラス
 */
class FifteenPuzzleSampleReducer :
    Reducer<FifteenPuzzleSampleUiState, FifteenPuzzleSampleMutation> {
    override fun reduce(
        currentState: FifteenPuzzleSampleUiState,
        mutation: FifteenPuzzleSampleMutation
    ): FifteenPuzzleSampleUiState {
        // TODO: 実際の変換処理
        return when (mutation) {
            FifteenPuzzleSampleMutation.ToInitialized -> FifteenPuzzleSampleUiState.Initialized
        }
    }
}