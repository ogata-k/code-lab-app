package com.ogata_k.mobile.code_lab.feature.home

import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * HomeReducerのテスト
 */
class HomeReducerTest {

    private val reducer = HomeReducer()

    @Test
    fun `ToInitializedミューテーションによりInitialized状態に遷移すること`() {
        val initialState = HomeUiState.UnInitialized
        val mutation = HomeMutation.ToInitialized

        val newState = reducer.reduce(initialState, mutation)

        assertEquals(HomeUiState.Initialized, newState)
    }
}
