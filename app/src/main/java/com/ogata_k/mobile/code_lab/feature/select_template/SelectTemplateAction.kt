package com.ogata_k.mobile.code_lab.feature.select_template

import com.ogata_k.mobile.code_lab.core.mvi.Action
import com.ogata_k.mobile.code_lab.core.mvi.ExecutionStrategy

/**
 * SelectTemplate featureの内部で処理されるアクション
 */
sealed interface SelectTemplateAction : Action {
    data object Initialize : SelectTemplateAction {
        override val strategy: ExecutionStrategy = ExecutionStrategy.Parallel
    }
}