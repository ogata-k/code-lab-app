package com.ogata_k.mobile.code_lab.feature.fifteen_puzzle_sample

import com.ogata_k.mobile.code_lab.core.mvi.CommonMutation
import com.ogata_k.mobile.code_lab.core.mvi.CommonUiEffect
import com.ogata_k.mobile.code_lab.core.mvi.StoreScope
import com.ogata_k.mobile.code_lab.domain.calculator.fifteen_puzzle_score.ScoreCalculator
import com.ogata_k.mobile.code_lab.domain.`class`.FifteenPuzzleBoard
import com.ogata_k.mobile.code_lab.domain.enum.FifteenPuzzleDifficulty
import com.ogata_k.mobile.code_lab.ui.widget.dialog.CommonDialogData
import com.ogata_k.mobile.code_lab.ui.widget.snackbar.CommonSnackbarMessage
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.unmockkObject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Test

/**
 * FifteenPuzzleSampleActionProcessorのテスト
 */
@OptIn(ExperimentalCoroutinesApi::class)
class FifteenPuzzleSampleActionProcessorTest {
    private val scoreCalculator = mockk<ScoreCalculator>(relaxed = true)
    private val actionProcessor = FifteenPuzzleSampleActionProcessor(scoreCalculator)
    private val scope: StoreScope<FifteenPuzzleSampleUiState, FifteenPuzzleSampleUiEffect, FifteenPuzzleSampleIntent, FifteenPuzzleSampleAction, FifteenPuzzleSampleMutation> =
        mockk(relaxed = true)

    @After
    fun tearDown() {
        unmockkObject(FifteenPuzzleBoard)
    }

    @Test
    fun `DismissDialogアクションによってダイアログが削除されること`() = runTest {
        val dialog = mockk<CommonDialogData>()
        val action = FifteenPuzzleSampleAction.DismissDialog(dialog)

        actionProcessor.process(action, scope)

        coVerify { scope.removeDialog(dialog) }
    }

    @Test
    fun `UpdateGridSizeSettingアクションによってUpdateGridSizeSettingミューテーションが発行されること`() =
        runTest {
            val action = FifteenPuzzleSampleAction.UpdateGridSizeSetting(4u)

            actionProcessor.process(action, scope)

            coVerify { scope.emitMutation(FifteenPuzzleSampleMutation.UpdateGridSizeSetting(4u)) }
        }

    @Test
    fun `UpdateDifficultySettingアクションによってUpdateDifficultySettingミューテーションが発行されること`() =
        runTest {
            val action =
                FifteenPuzzleSampleAction.UpdateDifficultySetting(FifteenPuzzleDifficulty.Hard)

            actionProcessor.process(action, scope)

            coVerify {
                scope.emitMutation(
                    FifteenPuzzleSampleMutation.UpdateDifficultySetting(
                        FifteenPuzzleDifficulty.Hard
                    )
                )
            }
        }

    @Test
    fun `ConfirmGameSettingBeforePlayアクションによって確認ダイアログが表示されること`() = runTest {
        val action = FifteenPuzzleSampleAction.ConfirmGameSettingBeforePlay
        coEvery { scope.getUiStateSnapshot() } returns FifteenPuzzleSampleUiState.NotStart(
            3u,
            FifteenPuzzleDifficulty.Easy
        )

        actionProcessor.process(action, scope)

        coVerify {
            scope.emitCommonMutation(match {
                it is CommonMutation.ReplaceDialog && it.data is CommonDialogData.ShowRequestActionDialog
            })
        }
    }

    @Test
    fun `StartPlayGameアクションによって盤面が生成され、ゲームが開始されること`() = runTest {
        val action = FifteenPuzzleSampleAction.StartPlayGame
        val board = mockk<FifteenPuzzleBoard>(relaxed = true)
        mockkObject(FifteenPuzzleBoard)
        every { FifteenPuzzleBoard.generateBoardForDifficulty(any(), any(), any()) } returns Pair(
            board,
            50u
        )
        coEvery { scope.getUiStateSnapshot() } returns FifteenPuzzleSampleUiState.NotStart(
            4u,
            FifteenPuzzleDifficulty.Normal
        )

        actionProcessor.process(action, scope)

        coVerify {
            scope.emitCommonMutation(any<CommonMutation.PushDialog>())
            scope.emitMutation(FifteenPuzzleSampleMutation.SetBoardAndStartPlay(board, 50u))
            scope.emitCommonMutation(any<CommonMutation.RemoveDialog>())
            scope.dispatchAction(FifteenPuzzleSampleAction.CheckInitialBoardDifficulty)
        }
    }

    @Test
    fun `CheckInitialBoardDifficultyアクションにおいて難易度が不一致の場合にスナックバーが表示されること`() =
        runTest {
            val action = FifteenPuzzleSampleAction.CheckInitialBoardDifficulty
            val board = mockk<FifteenPuzzleBoard>(relaxed = true)
            every { board.gridSize } returns 4u
            every { board.values } returns List(16) { it.toUInt() }
            every { board.difficulty } returns FifteenPuzzleDifficulty.Hard
            // 10uはEasy判定になると想定
            coEvery { scope.getUiStateSnapshot() } returns FifteenPuzzleSampleUiState.Playing(
                board,
                10u
            )

        actionProcessor.process(action, scope)

            coVerify {
                scope.emitCommonUiEffect(match {
                    it is CommonUiEffect.ShowSnackbar && it.data.message == CommonSnackbarMessage.DifficultyMismatch
                })
            }
    }

    @Test
    fun `RetryPlayGameByInvalidDifficultyアクションによって盤面が再生成されること`() = runTest {
        val action = FifteenPuzzleSampleAction.RetryPlayGameByInvalidDifficulty
        val board = mockk<FifteenPuzzleBoard>(relaxed = true)
        val oldBoard = mockk<FifteenPuzzleBoard>(relaxed = true)
        every { oldBoard.gridSize } returns 4u
        every { oldBoard.values } returns List(16) { it.toUInt() }
        every { oldBoard.difficulty } returns FifteenPuzzleDifficulty.Normal

        mockkObject(FifteenPuzzleBoard)
        every { FifteenPuzzleBoard.generateBoardForDifficulty(any(), any(), any()) } returns Pair(
            board,
            40u
        )
        coEvery { scope.getUiStateSnapshot() } returns FifteenPuzzleSampleUiState.Playing(oldBoard)

        actionProcessor.process(action, scope)

        coVerify {
            scope.emitCommonMutation(any<CommonMutation.PushDialog>())
            scope.emitMutation(FifteenPuzzleSampleMutation.SetBoardAndStartPlay(board, 40u))
            scope.emitCommonMutation(any<CommonMutation.RemoveDialog>())
            scope.dispatchAction(FifteenPuzzleSampleAction.CheckInitialBoardDifficulty)
        }
    }
}
