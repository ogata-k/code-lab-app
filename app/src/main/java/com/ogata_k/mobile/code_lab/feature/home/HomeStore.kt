package com.ogata_k.mobile.code_lab.feature.home

import com.ogata_k.mobile.code_lab.common.global_ui.GlobalUiController
import com.ogata_k.mobile.code_lab.core.mvi.BaseStore
import kotlinx.coroutines.CoroutineScope

/**
 * Home featureの状態管理を統括するクラス
 */
class HomeStore(
    scope: CoroutineScope,
    initialState: HomeUiState,
    actionProcessor: HomeActionProcessor,
    reducer: HomeReducer,
    globalUiController: GlobalUiController
) : BaseStore<HomeUiState, HomeUiEffect, HomeIntent, HomeAction, HomeMutation>(
    scope = scope,
    initialState = initialState,
    actionProcessor = actionProcessor,
    reducer = reducer,
    globalUiController = globalUiController
)