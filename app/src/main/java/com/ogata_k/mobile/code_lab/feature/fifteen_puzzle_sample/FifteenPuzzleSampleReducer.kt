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
        return when (mutation) {
            is FifteenPuzzleSampleMutation.UpdateGridSizeSetting -> if (currentState is FifteenPuzzleSampleUiState.NotStart) {
                currentState.copy(gridSize = mutation.gridSize)
            } else {
                currentState
            }

            is FifteenPuzzleSampleMutation.UpdateDifficultySetting -> if (currentState is FifteenPuzzleSampleUiState.NotStart) {
                currentState.copy(difficulty = mutation.difficulty)
            } else {
                currentState
            }

            is FifteenPuzzleSampleMutation.SetBoardAndStartPlay -> {
                val board = mutation.board
                val estimateBoardDifficulty = mutation.estimateBoardDifficulty
                // @todo スコア履歴とかを持っている場合は、ここでちゃんと渡すように指定を修正する必要がある。
                FifteenPuzzleSampleUiState.Playing(
                    board = board,
                    estimateBoardDifficulty = estimateBoardDifficulty,
                    stepCount = 0u
                )
            }
        }
    }
}