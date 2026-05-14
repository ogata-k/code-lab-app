package com.ogata_k.mobile.code_lab.feature.counter_sample

import com.ogata_k.mobile.code_lab.core.mvi.Action
import com.ogata_k.mobile.code_lab.core.mvi.DefaultExecutionKey
import com.ogata_k.mobile.code_lab.core.mvi.ExecutionStrategy

/**
 * CounterSample featureの内部で処理されるアクション
 */
sealed interface CounterSampleAction : Action {
    data class Increment(val amount: UInt) : CounterSampleAction {
        override val strategy: ExecutionStrategy = ExecutionStrategy.Sequential(DefaultExecutionKey)
    }

    data class Decrement(val amount: UInt) : CounterSampleAction {
        override val strategy: ExecutionStrategy = ExecutionStrategy.Sequential(DefaultExecutionKey)
    }
}