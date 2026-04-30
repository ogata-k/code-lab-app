package com.ogata_k.mobile.code_lab.feature.home

import com.ogata_k.mobile.code_lab.core.mvi.BaseStateManager

/**
 * Home の状態管理を統括するクラス
 */
class HomeStateManager(
    initialState: HomeUiState,
    actionProcessor: HomeActionProcessor,
    reducer: HomeReducer
) : BaseStateManager<HomeUiState, HomeUiEffect, HomeIntent, HomeAction, HomeMutation>(
    initialState = initialState,
    actionProcessor = actionProcessor,
    reducer = reducer
)