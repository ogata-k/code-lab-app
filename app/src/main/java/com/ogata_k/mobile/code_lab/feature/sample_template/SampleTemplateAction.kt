package com.ogata_k.mobile.code_lab.feature.sample_template

import com.ogata_k.mobile.code_lab.core.mvi.Action
import com.ogata_k.mobile.code_lab.core.mvi.DefaultExecutionKey
import com.ogata_k.mobile.code_lab.core.mvi.ExecutionStrategy
import com.ogata_k.mobile.code_lab.ui.enum.SampleFeatureDiv
import com.ogata_k.mobile.code_lab.ui.widget.dialog.CommonDialogData

/**
 * SampleTemplate featureの内部で処理されるアクション
 */
sealed interface SampleTemplateAction : Action {
    data class DismissDialog(val data: CommonDialogData) : SampleTemplateAction {
        override val strategy: ExecutionStrategy = ExecutionStrategy.Parallel
    }

    data class NavigateToSampleFeature(val sampleFeatureDiv: SampleFeatureDiv) :
        SampleTemplateAction {
        override val strategy: ExecutionStrategy = ExecutionStrategy.Sequential(DefaultExecutionKey)
    }
}