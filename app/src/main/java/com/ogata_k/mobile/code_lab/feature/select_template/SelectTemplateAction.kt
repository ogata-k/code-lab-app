package com.ogata_k.mobile.code_lab.feature.select_template

import com.ogata_k.mobile.code_lab.core.mvi.Action
import com.ogata_k.mobile.code_lab.core.mvi.DefaultExecutionKey
import com.ogata_k.mobile.code_lab.core.mvi.ExecutionStrategy
import com.ogata_k.mobile.code_lab.domain.enum.TemplateDiv

/**
 * SelectTemplate featureの内部で処理されるアクション
 */
sealed interface SelectTemplateAction : Action {
    data object Initialize : SelectTemplateAction {
        override val strategy: ExecutionStrategy = ExecutionStrategy.Parallel
    }

    data class NavigateToTemplate(val templateDiv: TemplateDiv) : SelectTemplateAction {
        override val strategy: ExecutionStrategy = ExecutionStrategy.Sequential(DefaultExecutionKey)
    }
}