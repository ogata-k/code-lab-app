package com.ogata_k.mobile.code_lab.feature.sample_template

import com.ogata_k.mobile.code_lab.core.mvi.Action
import com.ogata_k.mobile.code_lab.core.mvi.DefaultExecutionKey
import com.ogata_k.mobile.code_lab.core.mvi.ExecutionStrategy
import com.ogata_k.mobile.code_lab.ui.enum.SampleFeatureDiv

/**
 * SampleTemplate featureの内部で処理されるアクション
 */
sealed interface SampleTemplateAction : Action {
    data class NavigateToSampleFeature(val sampleFeatureDiv: SampleFeatureDiv) :
        SampleTemplateAction {
        override val strategy: ExecutionStrategy = ExecutionStrategy.Sequential(DefaultExecutionKey)
    }
}