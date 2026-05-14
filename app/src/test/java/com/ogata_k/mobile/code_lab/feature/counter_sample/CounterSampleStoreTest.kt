package com.ogata_k.mobile.code_lab.feature.counter_sample

import app.cash.turbine.test
import com.ogata_k.mobile.code_lab.core.mvi.ScreenState
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * CounterSampleStoreのテスト
 */
@OptIn(ExperimentalCoroutinesApi::class)
class CounterSampleStoreTest {
    @Test
    fun `初期状態のcountが0であること`() = runTest {
        val actionProcessor = CounterSampleActionProcessor()
        val store = CounterSampleStore(
            scope = backgroundScope,
            initialState = CounterSampleUiState(count = 0),
            actionProcessor = actionProcessor,
            reducer = CounterSampleReducer(),
            globalUiController = mockk()
        )

        assertEquals(
            ScreenState(featureUiState = CounterSampleUiState(count = 0)),
            store.uiState.value
        )
    }

    @Test
    fun `Incrementアクションによってcountがちょうど一つ増加すること`() = runTest {
        val actionProcessor = CounterSampleActionProcessor()
        val store = CounterSampleStore(
            scope = backgroundScope,
            initialState = CounterSampleUiState(count = 0),
            actionProcessor = actionProcessor,
            reducer = CounterSampleReducer(),
            globalUiController = mockk()
        )

        store.uiState.test {
            assertEquals(
                ScreenState(featureUiState = CounterSampleUiState(count = 0)),
                awaitItem()
            )

            store.dispatchAction(CounterSampleAction.Increment(1u))

            advanceUntilIdle()
            assertEquals(
                ScreenState(featureUiState = CounterSampleUiState(count = 1)),
                awaitItem()
            )
        }
    }
}
