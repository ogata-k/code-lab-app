package com.ogata_k.mobile.code_lab.feature.home

import com.ogata_k.mobile.code_lab.core.mvi.Action
import com.ogata_k.mobile.code_lab.core.mvi.ExecutionStrategy

/**
 * Home featureの内部で処理されるアクション
 */
sealed interface HomeAction : Action {
    data object Initialize : HomeAction {
        override val strategy: ExecutionStrategy = ExecutionStrategy.Parallel
    }
}