package com.ogata_k.mobile.code_lab.feature.home

import com.ogata_k.mobile.code_lab.core.mvi.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * Home の ViewModel
 */
@HiltViewModel
class HomeViewModel @Inject constructor(
    actionProcessor: HomeActionProcessor,
) : BaseViewModel<HomeUiState, HomeUiEffect, HomeIntent, HomeAction, HomeMutation>(
    stateManager = HomeStateManager(
        initialState = HomeUiState.UnInitialized,
        actionProcessor = actionProcessor,
        reducer = HomeReducer()
    )
) {
    init {
        // 初期データのロード
        dispatchAction(HomeAction.Initialize)
    }
}