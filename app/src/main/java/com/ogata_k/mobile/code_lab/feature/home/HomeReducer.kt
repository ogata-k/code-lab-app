package com.ogata_k.mobile.code_lab.feature.home
import com.ogata_k.mobile.code_lab.feature.Reducer

/**
 * Home の現在の状態とミューテーションから新しい状態を生成するクラス
 */
class HomeReducer : Reducer<HomeUiState, HomeMutation> {
    override fun reduce(
        currentState: HomeUiState,
        mutation: HomeMutation
    ): HomeUiState {
        return currentState
    }
}