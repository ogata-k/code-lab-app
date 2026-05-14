package com.ogata_k.mobile.code_lab.feature.sample_template

import com.ogata_k.mobile.code_lab.core.mvi.Action
import com.ogata_k.mobile.code_lab.core.mvi.ExecutionStrategy

/**
 * SampleTemplate featureの内部で処理されるアクション
 */
sealed interface SampleTemplateAction : Action {
    data object NavigateToCounter : SampleTemplateAction {
        override val strategy: ExecutionStrategy = ExecutionStrategy.Parallel
    }
}