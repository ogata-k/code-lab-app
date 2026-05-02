package com.ogata_k.mobile.code_lab.feature.home

import com.ogata_k.mobile.code_lab.core.mvi.BaseStateManager
import kotlinx.coroutines.CoroutineScope

/**
 * Home featureの状態管理を統括するクラス
 */
class HomeStateManager(
    scope: CoroutineScope,
    initialState: HomeUiState,
    actionProcessor: HomeActionProcessor,
    reducer: HomeReducer
) : BaseStateManager<HomeUiState, HomeUiEffect, HomeIntent, HomeAction, HomeMutation>(
    scope = scope,
    initialState = initialState,
    actionProcessor = actionProcessor,
    reducer = reducer
)