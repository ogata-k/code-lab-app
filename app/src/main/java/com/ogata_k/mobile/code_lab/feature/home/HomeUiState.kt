package com.ogata_k.mobile.code_lab.feature.home

import com.ogata_k.mobile.code_lab.core.mvi.UiState

/**
 * HomeのUI状態
 */
sealed interface HomeUiState : UiState {
    data object UnInitialized : HomeUiState
}