package com.ogata_k.mobile.code_lab.feature.home

import com.ogata_k.mobile.code_lab.feature.ActionProcessor
import com.ogata_k.mobile.code_lab.feature.StateManagerScope
import javax.inject.Inject

/**
 * Home のアクションを処理し、ミューテーションを生成するクラス
 */
class HomeActionProcessor @Inject constructor() :
    ActionProcessor<HomeUiState, HomeUiEffect, HomeAction, HomeMutation> {
    override suspend fun process(
        action: HomeAction,
        scope: StateManagerScope<HomeUiState, HomeUiEffect, HomeMutation>
    ) {
        when (action) {
            is HomeAction.Initialize -> {
                // TODO: 初期化処理
            }
        }
    }
}