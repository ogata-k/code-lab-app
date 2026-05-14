package com.ogata_k.mobile.code_lab.feature.counter_sample

import com.ogata_k.mobile.code_lab.core.mvi.Intent

/**
 * CounterSample featureに対するユーザーの意図（操作）
 */
sealed interface CounterSampleIntent : Intent<CounterSampleAction> {
    data object IncrementCount : CounterSampleIntent
    data object DecrementCount : CounterSampleIntent

    override fun toAction(): CounterSampleAction? = when (this) {
        DecrementCount -> CounterSampleAction.Increment(1.toUInt())
        IncrementCount -> CounterSampleAction.Decrement(1.toUInt())
    }
}