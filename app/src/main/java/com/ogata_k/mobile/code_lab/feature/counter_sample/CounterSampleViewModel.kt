package com.ogata_k.mobile.code_lab.feature.counter_sample

import androidx.lifecycle.viewModelScope
import com.ogata_k.mobile.code_lab.core.mvi.BaseViewModel
import com.ogata_k.mobile.code_lab.global.GlobalUiController
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * CounterSample featureのViewModel
 */
@HiltViewModel
class CounterSampleViewModel @Inject constructor(
    actionProcessor: CounterSampleActionProcessor,
    globalUiController: GlobalUiController
) : BaseViewModel<CounterSampleUiState, CounterSampleUiEffect, CounterSampleIntent, CounterSampleAction, CounterSampleMutation>() {
    override val store: CounterSampleStore = CounterSampleStore(
        scope = viewModelScope,
        // @todo 必要ならカウント初期値を変更できるようにする
        initialState = CounterSampleUiState(count = 0),
        actionProcessor = actionProcessor,
        reducer = CounterSampleReducer(),
        globalUiController = globalUiController
    )

    // 追加の初期化処理はない
}