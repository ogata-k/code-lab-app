package com.ogata_k.mobile.code_lab.feature.fifteen_puzzle_sample

import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * FifteenPuzzleSampleReducerのテスト
 */
class FifteenPuzzleSampleReducerTest {
    private val reducer = FifteenPuzzleSampleReducer()

    @Test
    fun `ToInitializedミューテーションによりInitialized状態に遷移すること`() {
        val initialState = FifteenPuzzleSampleUiState.UnInitialized
        val mutation = FifteenPuzzleSampleMutation.ToInitialized

        val newState = reducer.reduce(initialState, mutation)

        assertEquals(FifteenPuzzleSampleUiState.Initialized, newState)
    }
}