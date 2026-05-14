package com.ogata_k.mobile.code_lab.feature.counter_sample

import com.ogata_k.mobile.code_lab.core.mvi.ActionProcessor
import com.ogata_k.mobile.code_lab.core.mvi.StoreScope
import javax.inject.Inject

/**
 * CounterSample featureのアクションを処理し、ミューテーションを生成するクラス
 */
class CounterSampleActionProcessor @Inject constructor() :
    ActionProcessor<CounterSampleUiState, CounterSampleUiEffect, CounterSampleIntent, CounterSampleAction, CounterSampleMutation> {
    override suspend fun process(
        action: CounterSampleAction,
        scope: StoreScope<CounterSampleUiState, CounterSampleUiEffect, CounterSampleIntent, CounterSampleAction, CounterSampleMutation>
    ) {
        when (action) {
            is CounterSampleAction.Decrement -> {
                scope.emitMutation(CounterSampleMutation.AddCount(-action.amount.toInt()))
            }

            is CounterSampleAction.Increment -> {
                scope.emitMutation(CounterSampleMutation.AddCount(+action.amount.toInt()))
            }
        }
    }
}