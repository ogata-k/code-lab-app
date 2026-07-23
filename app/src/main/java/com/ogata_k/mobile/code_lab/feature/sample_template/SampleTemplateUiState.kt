package com.ogata_k.mobile.code_lab.feature.sample_template

import com.ogata_k.mobile.code_lab.core.mvi.UiState

/**
 * SampleTemplate featureのUI状態
 */
sealed interface SampleTemplateUiState : UiState {
    data object Initialized : SampleTemplateUiState
}