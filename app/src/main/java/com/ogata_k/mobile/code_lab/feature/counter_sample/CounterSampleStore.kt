package com.ogata_k.mobile.code_lab.feature.counter_sample

import com.ogata_k.mobile.code_lab.core.mvi.BaseStore
import com.ogata_k.mobile.code_lab.global.GlobalUiController
import kotlinx.coroutines.CoroutineScope

/**
 * CounterSample featureの状態管理を統括するクラス
 */
class CounterSampleStore(
    scope: CoroutineScope,
    initialState: CounterSampleUiState,
    actionProcessor: CounterSampleActionProcessor,
    reducer: CounterSampleReducer,
    globalUiController: GlobalUiController
) : BaseStore<CounterSampleUiState, CounterSampleUiEffect, CounterSampleIntent, CounterSampleAction, CounterSampleMutation>(
    scope = scope,
    initialState = initialState,
    actionProcessor = actionProcessor,
    reducer = reducer,
    globalUiController = globalUiController
)