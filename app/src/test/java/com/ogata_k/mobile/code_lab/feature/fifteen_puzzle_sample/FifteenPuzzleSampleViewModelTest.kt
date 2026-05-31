package com.ogata_k.mobile.code_lab.feature.fifteen_puzzle_sample

import app.cash.turbine.test
import com.ogata_k.mobile.code_lab.core.mvi.ScreenState
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * FifteenPuzzleSampleViewModelのテスト
 */
@OptIn(ExperimentalCoroutinesApi::class)
class FifteenPuzzleSampleViewModelTest {
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `初期化時にNotStart状態になること`() = runTest {
        val actionProcessor = FifteenPuzzleSampleActionProcessor(mockk(relaxed = true))
        val viewModel = FifteenPuzzleSampleViewModel(actionProcessor, mockk())

        viewModel.uiState.test {
            assertEquals(
                ScreenState(
                    featureUiState = FifteenPuzzleSampleUiState.NotStart(
                        4u,
                        com.ogata_k.mobile.code_lab.domain.enum.FifteenPuzzleDifficulty.Normal
                    )
                ),
                awaitItem()
            )
        }
    }

    @Test
    fun `確認ダイアログを表示し、Intent経由で閉じることができること`() = runTest {
        val actionProcessor = FifteenPuzzleSampleActionProcessor(mockk(relaxed = true))
        val viewModel = FifteenPuzzleSampleViewModel(actionProcessor, mockk())

        viewModel.uiState.test {
            // 1. 初期状態
            skipItems(1)

            // 2. 確認ダイアログの表示を要求
            viewModel.dispatchIntent(FifteenPuzzleSampleIntent.ConfirmGameSettingBeforePlay)
            advanceUntilIdle()

            val stateWithDialog = awaitItem()
            assertTrue(
                "ダイアログが表示されていること",
                stateWithDialog.localDialogQueue.isNotEmpty()
            )
            val dialog = stateWithDialog.localDialogQueue.first()

            // 3. Intent経由でダイアログを閉じる
            viewModel.dispatchIntent(FifteenPuzzleSampleIntent.DismissDialog(dialog))
            advanceUntilIdle()

            val stateEmpty = awaitItem()
            assertTrue("ダイアログが削除されていること", stateEmpty.localDialogQueue.isEmpty())
        }
    }
}
