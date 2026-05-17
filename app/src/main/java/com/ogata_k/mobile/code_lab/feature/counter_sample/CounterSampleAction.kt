package com.ogata_k.mobile.code_lab.feature.counter_sample

import com.ogata_k.mobile.code_lab.core.mvi.Action
import com.ogata_k.mobile.code_lab.core.mvi.DefaultExecutionKey
import com.ogata_k.mobile.code_lab.core.mvi.ExecutionStrategy
import com.ogata_k.mobile.code_lab.feature.counter_sample.enum.SlideOffsetDivisorType

/**
 * CounterSample featureの内部で処理されるアクション
 */
sealed interface CounterSampleAction : Action {
    data class IncrementCount(val amount: UInt) : CounterSampleAction {
        override val strategy: ExecutionStrategy = ExecutionStrategy.Sequential(DefaultExecutionKey)
    }

    data class DecrementCount(val amount: UInt) : CounterSampleAction {
        override val strategy: ExecutionStrategy = ExecutionStrategy.Sequential(DefaultExecutionKey)
    }

    data class UpdateSlideDuration(val slideDurationMs: UInt) : CounterSampleAction {
        override val strategy: ExecutionStrategy = ExecutionStrategy.Sequential(DefaultExecutionKey)
    }

    data class UpdateFadeDuration(val fadeDurationMs: UInt) : CounterSampleAction {
        override val strategy: ExecutionStrategy = ExecutionStrategy.Sequential(DefaultExecutionKey)
    }

    data class UpdateSlideOffsetDivisor(val offsetDivisorType: SlideOffsetDivisorType) :
        CounterSampleAction {
        override val strategy: ExecutionStrategy = ExecutionStrategy.Sequential(DefaultExecutionKey)
    }
}