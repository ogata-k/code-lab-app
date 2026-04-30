package com.ogata_k.mobile.code_lab.feature.home

import com.ogata_k.mobile.code_lab.core.mvi.Reducer

/**
 * Home の現在の状態とミューテーションから新しい状態を生成するクラス
 */
class HomeReducer : Reducer<HomeUiState, HomeMutation> {
    override fun reduce(
        currentState: HomeUiState,
        mutation: HomeMutation
    ): HomeUiState {
        // TODO: 実際の変換処理
        return currentState
    }
}