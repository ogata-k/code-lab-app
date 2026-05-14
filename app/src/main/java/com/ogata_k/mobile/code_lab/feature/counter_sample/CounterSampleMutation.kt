package com.ogata_k.mobile.code_lab.feature.counter_sample

import com.ogata_k.mobile.code_lab.core.mvi.Mutation

/**
 * CounterSample featureの状態を変更するための変更内容
 */
sealed interface CounterSampleMutation : Mutation {
    data class AddCount(val diff: Int) : CounterSampleMutation
}