package com.ogata_k.mobile.code_lab.feature.select_template

import com.ogata_k.mobile.code_lab.core.mvi.UiState

/**
 * SelectTemplate featureのUI状態
 */
sealed interface SelectTemplateUiState : UiState {
    data object UnInitialized : SelectTemplateUiState
    data object Initialized : SelectTemplateUiState
}
