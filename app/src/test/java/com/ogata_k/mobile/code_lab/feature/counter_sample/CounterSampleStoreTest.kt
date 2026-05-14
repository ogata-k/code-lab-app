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
    fun `初期状態がUnInitializedであること`() = runTest {
        val actionProcessor = CounterSampleActionProcessor()
        val store = CounterSampleStore(
            scope = backgroundScope,
            initialState = CounterSampleUiState.UnInitialized,
            actionProcessor = actionProcessor,
            reducer = CounterSampleReducer(),
            globalUiController = mockk()
        )

        assertEquals(
            ScreenState(featureUiState = CounterSampleUiState.UnInitialized),
            store.uiState.value
        )
    }

    @Test
    fun `Initializeアクションによって状態がInitializedに更新されること`() = runTest {
        val actionProcessor = CounterSampleActionProcessor()
        val store = CounterSampleStore(
            scope = backgroundScope,
            initialState = CounterSampleUiState.UnInitialized,
            actionProcessor = actionProcessor,
            reducer = CounterSampleReducer(),
            globalUiController = mockk()
        )

        store.uiState.test {
            assertEquals(
                ScreenState(featureUiState = CounterSampleUiState.UnInitialized),
                awaitItem()
            )

            store.dispatchAction(CounterSampleAction.Initialize)

            advanceUntilIdle()
            assertEquals(
                ScreenState(featureUiState = CounterSampleUiState.Initialized),
                awaitItem()
            )
        }
    }
}