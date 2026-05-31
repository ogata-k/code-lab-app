package com.ogata_k.mobile.code_lab.feature.fifteen_puzzle_sample

import app.cash.turbine.test
import com.ogata_k.mobile.code_lab.core.mvi.ScreenState
import com.ogata_k.mobile.code_lab.domain.enum.FifteenPuzzleDifficulty
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
    fun `初期状態がNotStartであること`() = runTest {
        val actionProcessor = FifteenPuzzleSampleActionProcessor(mockk(relaxed = true))
        val store = FifteenPuzzleSampleStore(
            scope = backgroundScope,
            initialState = FifteenPuzzleSampleUiState.NotStart(),
            actionProcessor = actionProcessor,
            reducer = FifteenPuzzleSampleReducer(),
            globalUiController = mockk()
        )

        assertEquals(
            ScreenState(featureUiState = FifteenPuzzleSampleUiState.NotStart()),
            store.uiState.value
        )
    }

    @Test
    fun `ChangeGridSizeインテントによって状態が更新されること`() = runTest {
        val actionProcessor = FifteenPuzzleSampleActionProcessor(mockk(relaxed = true))
        val store = FifteenPuzzleSampleStore(
            scope = backgroundScope,
            initialState = FifteenPuzzleSampleUiState.NotStart(gridSize = 4u),
            actionProcessor = actionProcessor,
            reducer = FifteenPuzzleSampleReducer(),
            globalUiController = mockk()
        )

        store.uiState.test {
            assertEquals(
                ScreenState(featureUiState = FifteenPuzzleSampleUiState.NotStart(gridSize = 4u)),
                awaitItem()
            )

            store.dispatchIntent(FifteenPuzzleSampleIntent.ChangeGridSize(5u))

            advanceUntilIdle()
            assertEquals(
                ScreenState(featureUiState = FifteenPuzzleSampleUiState.NotStart(gridSize = 5u)),
                awaitItem()
            )
        }
    }

    @Test
    fun `ChangeDifficultyインテントによって状態が更新されること`() = runTest {
        val actionProcessor = FifteenPuzzleSampleActionProcessor(mockk(relaxed = true))
        val store = FifteenPuzzleSampleStore(
            scope = backgroundScope,
            initialState = FifteenPuzzleSampleUiState.NotStart(difficulty = FifteenPuzzleDifficulty.Normal),
            actionProcessor = actionProcessor,
            reducer = FifteenPuzzleSampleReducer(),
            globalUiController = mockk()
        )

        store.uiState.test {
            assertEquals(
                ScreenState(featureUiState = FifteenPuzzleSampleUiState.NotStart(difficulty = FifteenPuzzleDifficulty.Normal)),
                awaitItem()
            )

            store.dispatchIntent(FifteenPuzzleSampleIntent.ChangeDifficulty(FifteenPuzzleDifficulty.Hard))

            advanceUntilIdle()
            assertEquals(
                ScreenState(featureUiState = FifteenPuzzleSampleUiState.NotStart(difficulty = FifteenPuzzleDifficulty.Hard)),
                awaitItem()
            )
        }
    }
}
