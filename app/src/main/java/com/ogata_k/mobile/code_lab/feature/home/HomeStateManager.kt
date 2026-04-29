package com.ogata_k.mobile.code_lab.feature.home

import com.ogata_k.mobile.code_lab.feature.BaseStateManager
import com.ogata_k.mobile.code_lab.feature.StateManagerScope
import javax.inject.Inject

/**
 * Home の状態管理を統括するクラス
 */
class HomeStateManager @Inject constructor(
    private val processor: HomeActionProcessor
) : BaseStateManager<HomeUiState, HomeUiEffect, HomeIntent, HomeAction, HomeMutation>() {
    override fun mapIntentToAction(intent: HomeIntent): HomeAction {
        TODO("Not yet implemented")
    }

    override suspend fun handleAction(
        action: HomeAction,
        scope: StateManagerScope<HomeUiState, HomeUiEffect, HomeMutation>
    ) {
        TODO("Not yet implemented")
    }
}