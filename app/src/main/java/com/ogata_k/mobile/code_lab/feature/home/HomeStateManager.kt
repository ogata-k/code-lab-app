package com.ogata_k.mobile.code_lab.feature.home
import com.ogata_k.mobile.code_lab.feature.BaseStateManager
import javax.inject.Inject

/**
 * Home の状態管理を統括するクラス
 */
class HomeStateManager @Inject constructor(
    processor: HomeActionProcessor,
    reducer: HomeReducer
) : BaseStateManager<HomeUiState, HomeUiEffect, HomeIntent, HomeAction, HomeMutation>(
    initialState = HomeUiState.UnInitialized,
    actionProcessor = processor,
    reducer = reducer
)