package com.ogata_k.mobile.code_lab.feature.fifteen_puzzle_sample

import com.ogata_k.mobile.code_lab.core.mvi.ActionProcessor
import com.ogata_k.mobile.code_lab.core.mvi.CommonMutation.PushDialog
import com.ogata_k.mobile.code_lab.core.mvi.CommonMutation.RemoveDialog
import com.ogata_k.mobile.code_lab.core.mvi.CommonMutation.ReplaceDialog
import com.ogata_k.mobile.code_lab.core.mvi.CommonUiEffect.ShowSnackbar
import com.ogata_k.mobile.code_lab.core.mvi.StoreScope
import com.ogata_k.mobile.code_lab.domain.calculator.fifteen_puzzle_score.FifteenPuzzleScoreEstimator
import com.ogata_k.mobile.code_lab.domain.calculator.fifteen_puzzle_score.ScoreCalculator
import com.ogata_k.mobile.code_lab.domain.`class`.FifteenPuzzleBoard
import com.ogata_k.mobile.code_lab.domain.enum.FifteenPuzzleDifficulty
import com.ogata_k.mobile.code_lab.feature.fifteen_puzzle_sample.FifteenPuzzleSampleMutation.SetBoardAndStartPlay
import com.ogata_k.mobile.code_lab.feature.fifteen_puzzle_sample.FifteenPuzzleSampleMutation.UpdateDifficultySetting
import com.ogata_k.mobile.code_lab.feature.fifteen_puzzle_sample.FifteenPuzzleSampleMutation.UpdateGridSizeSetting
import com.ogata_k.mobile.code_lab.ui.widget.dialog.CommonDialogButtonText
import com.ogata_k.mobile.code_lab.ui.widget.dialog.CommonDialogData.ShowLoading
import com.ogata_k.mobile.code_lab.ui.widget.dialog.CommonDialogData.ShowRequestActionDialog
import com.ogata_k.mobile.code_lab.ui.widget.dialog.CommonDialogMessage.ConfirmStartFifteenPuzzleGame
import com.ogata_k.mobile.code_lab.ui.widget.dialog.CommonDialogTitle
import com.ogata_k.mobile.code_lab.ui.widget.snackbar.CommonSnackbarData
import com.ogata_k.mobile.code_lab.ui.widget.snackbar.CommonSnackbarLabel
import com.ogata_k.mobile.code_lab.ui.widget.snackbar.CommonSnackbarMessage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * FifteenPuzzleSample featureのアクションを処理し、ミューテーションを生成するクラス
 */
class FifteenPuzzleSampleActionProcessor @Inject constructor(
    private val scoreCalculator: ScoreCalculator
) :
    ActionProcessor<FifteenPuzzleSampleUiState, FifteenPuzzleSampleUiEffect, FifteenPuzzleSampleIntent, FifteenPuzzleSampleAction, FifteenPuzzleSampleMutation> {
    override suspend fun process(
        action: FifteenPuzzleSampleAction,
        scope: StoreScope<FifteenPuzzleSampleUiState, FifteenPuzzleSampleUiEffect, FifteenPuzzleSampleIntent, FifteenPuzzleSampleAction, FifteenPuzzleSampleMutation>
    ) {
        when (action) {
            is FifteenPuzzleSampleAction.DismissDialog -> {
                scope.removeDialog(action.data)
            }

            is FifteenPuzzleSampleAction.UpdateGridSizeSetting -> {
                scope.emitMutation(UpdateGridSizeSetting(action.gridSize))
            }

            is FifteenPuzzleSampleAction.UpdateDifficultySetting -> {
                scope.emitMutation(UpdateDifficultySetting(action.difficulty))
            }

            FifteenPuzzleSampleAction.ConfirmGameSettingBeforePlay -> {
                val currentUiState = scope.getUiStateSnapshot()
                val (gridSize: UInt, difficulty: FifteenPuzzleDifficulty) = when (currentUiState) {
                    is FifteenPuzzleSampleUiState.NotStart -> Pair(
                        currentUiState.gridSize,
                        currentUiState.difficulty
                    )

                    is FifteenPuzzleSampleUiState.Playing -> Pair(
                        currentUiState.board.gridSize,
                        currentUiState.board.difficulty
                    )

                    is FifteenPuzzleSampleUiState.GameCleared -> return
                }

                scope.emitCommonMutation(
                    ReplaceDialog(
                        data = ShowRequestActionDialog(
                            title = CommonDialogTitle.Confirm,
                            message = ConfirmStartFifteenPuzzleGame(
                                gridSize = gridSize,
                                difficulty = difficulty,
                                estimateMaxScore = FifteenPuzzleScoreEstimator.estimateMaxScore(
                                    calculator = scoreCalculator,
                                    gridSize = gridSize,
                                    difficulty = difficulty
                                ),
                            ),
                            actionButtonText = CommonDialogButtonText.Start,
                            onAction = { dialog ->
                                scope.removeDialog(dialog)
                                scope.intentCallback(FifteenPuzzleSampleIntent.StartPlayGame)
                                    .invoke()
                            },
                        ),
                    )
                )
            }

            FifteenPuzzleSampleAction.StartPlayGame -> {
                val currentUiState = scope.getUiStateSnapshot()
                if (currentUiState !is FifteenPuzzleSampleUiState.NotStart) {
                    scope.emitCommonUiEffect(
                        ShowSnackbar(
                            CommonSnackbarData(
                                message = CommonSnackbarMessage.InvalidState
                            )
                        )
                    )
                    return
                }
                val loading = ShowLoading()
                scope.emitCommonMutation(PushDialog(loading))
                // 【重要】バックグラウンドスレッドで重い計算を実行
                // これによりメインスレッドが空き、上のローディングを描画できる
                val (board, estimateBoardDifficulty) = withContext(Dispatchers.Default) {
                    FifteenPuzzleBoard.generateBoardForDifficulty(
                        currentUiState.gridSize,
                        currentUiState.difficulty
                    )
                }
                scope.emitMutation(SetBoardAndStartPlay(board, estimateBoardDifficulty))
                scope.emitCommonMutation(RemoveDialog(loading))
                scope.dispatchAction(FifteenPuzzleSampleAction.CheckInitialBoardDifficulty)
            }

            FifteenPuzzleSampleAction.CheckInitialBoardDifficulty -> {
                val state = scope.getUiStateSnapshot()
                if (state is FifteenPuzzleSampleUiState.Playing) {
                    val estimateDifficulty =
                        FifteenPuzzleDifficulty.fromDifficultyValue(state.estimateBoardDifficulty)
                    if (state.board.difficulty != estimateDifficulty) {
                        scope.emitCommonUiEffect(
                            ShowSnackbar(
                                CommonSnackbarData(
                                    message = CommonSnackbarMessage.DifficultyMismatch,
                                    actionLabel = CommonSnackbarLabel.Retry,
                                    onAction = scope.intentCallback(FifteenPuzzleSampleIntent.RetryPlayGameByInvalidDifficulty)
                                )
                            )
                        )
                    }
                }
            }

            FifteenPuzzleSampleAction.RetryPlayGameByInvalidDifficulty -> {
                val currentUiState = scope.getUiStateSnapshot()
                if (currentUiState !is FifteenPuzzleSampleUiState.Playing) {
                    scope.emitCommonUiEffect(
                        ShowSnackbar(
                            CommonSnackbarData(
                                message = CommonSnackbarMessage.InvalidState
                            )
                        )
                    )
                    return
                }
                val loading = ShowLoading()
                scope.emitCommonMutation(PushDialog(loading))
                // 【重要】バックグラウンドスレッドで重い計算を実行
                // これによりメインスレッドが空き、上のローディングを描画できる
                val (board, estimateBoardDifficulty) = withContext(Dispatchers.Default) {
                    FifteenPuzzleBoard.generateBoardForDifficulty(
                        currentUiState.board.gridSize,
                        currentUiState.board.difficulty
                    )
                }
                scope.emitMutation(SetBoardAndStartPlay(board, estimateBoardDifficulty))
                scope.emitCommonMutation(RemoveDialog(loading))
                scope.dispatchAction(FifteenPuzzleSampleAction.CheckInitialBoardDifficulty)
            }
        }
    }
}