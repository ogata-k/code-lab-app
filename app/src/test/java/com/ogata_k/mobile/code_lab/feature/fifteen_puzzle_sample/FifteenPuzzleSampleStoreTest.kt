package com.ogata_k.mobile.code_lab.feature.fifteen_puzzle_sample

import app.cash.turbine.test
import com.ogata_k.mobile.code_lab.core.mvi.ScreenState
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * FifteenPuzzleSampleStoreのテスト
 */
@OptIn(ExperimentalCoroutinesApi::class)
class FifteenPuzzleSampleStoreTest {
    @Test
    fun `初期状態がUnInitializedであること`() = runTest {
        val actionProcessor = FifteenPuzzleSampleActionProcessor()
        val store = FifteenPuzzleSampleStore(
            scope = backgroundScope,
            initialState = FifteenPuzzleSampleUiState.UnInitialized,
            actionProcessor = actionProcessor,
            reducer = FifteenPuzzleSampleReducer(),
            globalUiController = mockk()
        )

        assertEquals(
            ScreenState(featureUiState = FifteenPuzzleSampleUiState.UnInitialized),
            store.uiState.value
        )
    }

    @Test
    fun `Initializeアクションによって状態がInitializedに更新されること`() = runTest {
        val actionProcessor = FifteenPuzzleSampleActionProcessor()
        val store = FifteenPuzzleSampleStore(
            scope = backgroundScope,
            initialState = FifteenPuzzleSampleUiState.UnInitialized,
            actionProcessor = actionProcessor,
            reducer = FifteenPuzzleSampleReducer(),
            globalUiController = mockk()
        )

        store.uiState.test {
            assertEquals(
                ScreenState(featureUiState = FifteenPuzzleSampleUiState.UnInitialized),
                awaitItem()
            )

            store.dispatchAction(FifteenPuzzleSampleAction.Initialize)

            advanceUntilIdle()
            assertEquals(
                ScreenState(featureUiState = FifteenPuzzleSampleUiState.Initialized),
                awaitItem()
            )
        }
    }
}