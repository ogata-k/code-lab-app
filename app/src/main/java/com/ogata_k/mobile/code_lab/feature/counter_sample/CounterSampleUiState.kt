package com.ogata_k.mobile.code_lab.feature.counter_sample

import com.ogata_k.mobile.code_lab.core.mvi.UiState

/**
 * CounterSample featureのUI状態
 */
data class CounterSampleUiState(val count: Int) : UiState