package com.ogata_k.mobile.code_lab.feature.fifteen_puzzle_sample

import com.ogata_k.mobile.code_lab.core.mvi.UiState
import com.ogata_k.mobile.code_lab.domain.`class`.FifteenPuzzleBoard
import com.ogata_k.mobile.code_lab.domain.enum.FifteenPuzzleDifficulty

/**
 * FifteenPuzzleSample featureのUI状態
 * １５パズルとは言っているが、サイズは可変を想定している。
 */
sealed interface FifteenPuzzleSampleUiState : UiState {
    data class NotStart(
        val gridSize: UInt = 4u,
        val difficulty: FifteenPuzzleDifficulty = FifteenPuzzleDifficulty.Normal,
    ) :
        FifteenPuzzleSampleUiState

    data class Playing(
        val board: FifteenPuzzleBoard,
        /**
         * ゲームの難易度。
         * 難易度は難易度計算の時に0-100の範囲で正規化して求められる。
         */
        val estimateBoardDifficulty: UInt = FifteenPuzzleDifficulty.estimateDifficultyValue(
            board
        ),
        val stepCount: UInt = 0u,
    ) : FifteenPuzzleSampleUiState {
    }

    data class GameCleared(
        val gridSize: UInt,
        val stepCount: UInt,
        val score: UInt,
    ) : FifteenPuzzleSampleUiState {
    }
}
