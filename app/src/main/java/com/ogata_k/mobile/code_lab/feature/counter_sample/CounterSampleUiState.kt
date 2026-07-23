package com.ogata_k.mobile.code_lab.feature.counter_sample

import com.ogata_k.mobile.code_lab.core.mvi.UiState
import com.ogata_k.mobile.code_lab.feature.counter_sample.enum.SlideOffsetDivisorType

/**
 * CounterSample featureのUI状態
 */
data class CounterSampleUiState(
    val count: Int,
    // try 120 (snappy) / 800 (luxurious)
    val slideDurationMs: UInt = 650u,

    // try 0 (no fade) / 600 (long crossfade)
    val fadeDurationMs: UInt = 450u,

    val slideOffsetDivisor: SlideOffsetDivisorType = SlideOffsetDivisorType.Full,
) : UiState