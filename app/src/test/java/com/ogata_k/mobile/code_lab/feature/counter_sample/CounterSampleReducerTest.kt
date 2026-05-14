package com.ogata_k.mobile.code_lab.feature.counter_sample

import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * CounterSampleReducerのテスト
 */
class CounterSampleReducerTest {
    private val reducer = CounterSampleReducer()

    @Test
    fun `AddCountミューテーションによりcountが更新されること（正数）`() {
        val initialState = CounterSampleUiState(count = 0)
        val mutation = CounterSampleMutation.AddCount(diff = 1)

        val newState = reducer.reduce(initialState, mutation)

        assertEquals(CounterSampleUiState(count = 1), newState)
    }

    @Test
    fun `AddCountミューテーションにより指定された分だけcountが更新されること（正数）`() {
        val initialState = CounterSampleUiState(count = 0)
        val mutation = CounterSampleMutation.AddCount(diff = 7)

        val newState = reducer.reduce(initialState, mutation)

        assertEquals(CounterSampleUiState(count = 7), newState)
    }

    @Test
    fun `AddCountミューテーションによりcountが更新されること（負数）`() {
        val initialState = CounterSampleUiState(count = 0)
        val mutation = CounterSampleMutation.AddCount(diff = -1)

        val newState = reducer.reduce(initialState, mutation)

        assertEquals(CounterSampleUiState(count = -1), newState)
    }

    @Test
    fun `AddCountミューテーションにより指定された分だけcountが更新されること（負数）`() {
        val initialState = CounterSampleUiState(count = 0)
        val mutation = CounterSampleMutation.AddCount(diff = -3)

        val newState = reducer.reduce(initialState, mutation)

        assertEquals(CounterSampleUiState(count = -3), newState)
    }

    @Test
    fun `AddCountミューテーションにより指定された分だけcountが更新されること`() {
        var count = 0
        val initialState = CounterSampleUiState(count = count)
        var newState = initialState
        listOf(1, 2, 5, -2, 3, -11).forEach {
            count += it
            val mutation = CounterSampleMutation.AddCount(diff = it)
            newState = reducer.reduce(newState, mutation)
        }

        assertEquals(CounterSampleUiState(count = count), newState)
    }
}
