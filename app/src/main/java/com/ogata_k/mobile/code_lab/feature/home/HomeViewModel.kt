package com.ogata_k.mobile.code_lab.feature.home

import androidx.lifecycle.viewModelScope
import com.ogata_k.mobile.code_lab.common.global_ui.GlobalUiController
import com.ogata_k.mobile.code_lab.core.mvi.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * Home featureのViewModel
 */
@HiltViewModel
class HomeViewModel @Inject constructor(
    actionProcessor: HomeActionProcessor,
    globalUiController: GlobalUiController,
) : BaseViewModel<HomeUiState, HomeUiEffect, HomeIntent, HomeAction, HomeMutation>() {
    override val stateManager: HomeStateManager = HomeStateManager(
        scope = viewModelScope,
        initialState = HomeUiState.UnInitialized,
        actionProcessor = actionProcessor,
        reducer = HomeReducer(),
        globalUiController = globalUiController
    )

    init {
        // 初期データのロード
        dispatchAction(HomeAction.Initialize)
    }
}