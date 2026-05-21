package com.ogata_k.mobile.code_lab.feature.fifteen_puzzle_sample

import com.ogata_k.mobile.code_lab.core.mvi.UiState

/**
 * FifteenPuzzleSample featureのUI状態
 */
sealed interface FifteenPuzzleSampleUiState : UiState {
    // TODO: 本来のUiStateに書き換える
    data object UnInitialized : FifteenPuzzleSampleUiState
    data object Initialized : FifteenPuzzleSampleUiState
}