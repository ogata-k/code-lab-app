package com.ogata_k.mobile.code_lab.feature.sample_template

import com.ogata_k.mobile.code_lab.core.mvi.UiEffect
import com.ogata_k.mobile.code_lab.ui.enum.SampleFeatureDiv

/**
 * SampleTemplate featureのUI副作用（ワンショットのイベント）
 */
sealed interface SampleTemplateUiEffect : UiEffect {
    data class NavigateToSampleFeature(val sampleFeatureDiv: SampleFeatureDiv) :
        SampleTemplateUiEffect
}