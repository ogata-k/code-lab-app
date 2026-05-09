package com.ogata_k.mobile.code_lab.feature.sample_template

import com.ogata_k.mobile.code_lab.core.mvi.Action
import com.ogata_k.mobile.code_lab.core.mvi.ExecutionStrategy

/**
 * SampleTemplate featureの内部で処理されるアクション
 */
sealed interface SampleTemplateAction : Action {
    // TODO: 本来のActionに書き換える
    data object Initialize : SampleTemplateAction {
        override val strategy: ExecutionStrategy = ExecutionStrategy.Parallel
    }
}