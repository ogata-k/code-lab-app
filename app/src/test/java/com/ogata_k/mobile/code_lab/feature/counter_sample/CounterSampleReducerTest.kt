package com.ogata_k.mobile.code_lab.feature.counter_sample

import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * CounterSampleReducerのテスト
 */
class CounterSampleReducerTest {
    private val reducer = CounterSampleReducer()

    @Test
    fun `ToInitializedミューテーションによりInitialized状態に遷移すること`() {
        val initialState = CounterSampleUiState.UnInitialized
        val mutation = CounterSampleMutation.ToInitialized

        val newState = reducer.reduce(initialState, mutation)

        assertEquals(CounterSampleUiState.Initialized, newState)
    }
}