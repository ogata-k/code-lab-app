package com.ogata_k.mobile.code_lab.feature.fifteen_puzzle_sample

import androidx.compose.ui.window.DialogProperties
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
import com.ogata_k.mobile.code_lab.feature.fifteen_puzzle_sample.FifteenPuzzleSampleMutation.GameCleared
import com.ogata_k.mobile.code_lab.feature.fifteen_puzzle_sample.FifteenPuzzleSampleMutation.IncrementBoardState
import com.ogata_k.mobile.code_lab.feature.fifteen_puzzle_sample.FifteenPuzzleSampleMutation.SetBoardAndStartPlay
import com.ogata_k.mobile.code_lab.feature.fifteen_puzzle_sample.FifteenPuzzleSampleMutation.SetToSettingForm
import com.ogata_k.mobile.code_lab.feature.fifteen_puzzle_sample.FifteenPuzzleSampleMutation.UpdateDifficultySetting
import com.ogata_k.mobile.code_lab.feature.fifteen_puzzle_sample.FifteenPuzzleSampleMutation.UpdateGridSizeSetting
import com.ogata_k.mobile.code_lab.feature.fifteen_puzzle_sample.FifteenPuzzleSampleUiEffect.PlayClearAnimation
import com.ogata_k.mobile.code_lab.feature.fifteen_puzzle_sample.FifteenPuzzleSampleUiEffect.ShowDifficultyMismatchError
import com.ogata_k.mobile.code_lab.ui.widget.dialog.CommonDialogButtonText
import com.ogata_k.mobile.code_lab.ui.widget.dialog.CommonDialogData.ShowLoading
import com.ogata_k.mobile.code_lab.ui.widget.dialog.CommonDialogData.ShowRequestActionDialog
import com.ogata_k.mobile.code_lab.ui.widget.dialog.CommonDialogMessage.ConfirmStartFifteenPuzzleGame
import com.ogata_k.mobile.code_lab.ui.widget.dialog.CommonDialogMessage.RequestNavigateToNextGame
import com.ogata_k.mobile.code_lab.ui.widget.dialog.CommonDialogTitle
import com.ogata_k.mobile.code_lab.ui.widget.snackbar.CommonSnackbarData
import com.ogata_k.mobile.code_lab.ui.widget.snackbar.CommonSnackbarMessage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

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
                        scope.emitUiEffect(ShowDifficultyMismatchError)
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

            is FifteenPuzzleSampleAction.MoveCell -> {
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
                if (currentUiState.board.isGoal()) {
                    // 完成した盤面なので移動させない
                    return
                }

                // 移動した後の盤面を求める。移動できなかった場合は、後続の処理の必要はないのでここでreturnする。
                val nextBoard =
                    currentUiState.board.move(action.cellValue) ?: return

                scope.emitMutation(IncrementBoardState(nextBoard))

                // ゲームクリア判定の処理
                if (nextBoard.isGoal()) {
                    val navigateToClearStateDuration = 2000L.milliseconds
                    // 盤面完成時の演出を要求
                    scope.emitUiEffect(PlayClearAnimation(navigateToClearStateDuration))

                    // 演出が終わるのを待ってからスコア表示（FinishGame）へ
                    // アニメーションの時間分だけ待機
                    delay(navigateToClearStateDuration)
                    scope.dispatchAction(FifteenPuzzleSampleAction.FinishGame)
                }
            }

            FifteenPuzzleSampleAction.FinishGame -> {
                val state = scope.getUiStateSnapshot()
                if (state is FifteenPuzzleSampleUiState.Playing) {
                    val score = scoreCalculator.calculate(
                        gridSize = state.board.gridSize,
                        difficulty = state.board.difficulty,
                        estimateDifficultyValue = state.estimateBoardDifficulty,
                        stepCount = state.stepCount
                    )
                    scope.emitMutation(
                        GameCleared(
                            gridSize = state.board.gridSize,
                            difficulty = state.board.difficulty,
                            estimateBoardDifficulty = state.estimateBoardDifficulty,
                            estimateStepCount = FifteenPuzzleScoreEstimator.estimateIdealStepCount(
                                state.board.gridSize,
                                state.board.difficulty
                            ),
                            stepCount = state.stepCount,
                            score = score,
                        )
                    )
                }
            }

            FifteenPuzzleSampleAction.RequestNavigateToNextGame -> {
                val currentUiState = scope.getUiStateSnapshot()
                if (currentUiState !is FifteenPuzzleSampleUiState.GameCleared) {
                    scope.emitCommonUiEffect(
                        ShowSnackbar(
                            CommonSnackbarData(
                                message = CommonSnackbarMessage.InvalidState
                            )
                        )
                    )
                    return
                }

                scope.emitCommonMutation(
                    ReplaceDialog(
                        data = ShowRequestActionDialog(
                            title = CommonDialogTitle.Confirm,
                            message = RequestNavigateToNextGame(
                                gridSize = currentUiState.gridSize,
                                difficulty = currentUiState.difficulty,
                                estimateMaxScore = FifteenPuzzleScoreEstimator.estimateMaxScore(
                                    calculator = scoreCalculator,
                                    gridSize = currentUiState.gridSize,
                                    difficulty = currentUiState.difficulty
                                ),
                            ),
                            cancelButtonText = CommonDialogButtonText.ChangeSetting,
                            onCancel = { dialog ->
                                scope.removeDialog(dialog)
                                scope.intentCallback(FifteenPuzzleSampleIntent.NavigateToChangeSettingFormForRetry)
                                    .invoke()
                            },
                            actionButtonText = CommonDialogButtonText.Retry,
                            onAction = { dialog ->
                                scope.removeDialog(dialog)
                                scope.intentCallback(FifteenPuzzleSampleIntent.RetryGameWithSameSetting)
                                    .invoke()
                            },
                            properties = DialogProperties(
                                // スコア確認に戻りたいことがあると思うので、外側タップでダイアログを閉じられるようにしておく
                                dismissOnClickOutside = true,
                                dismissOnBackPress = true,
                            ),
                        ),
                    )
                )
            }

            FifteenPuzzleSampleAction.NavigateToChangeSettingFormForRetry -> {
                val currentUiState = scope.getUiStateSnapshot()
                if (currentUiState !is FifteenPuzzleSampleUiState.GameCleared) {
                    scope.emitCommonUiEffect(
                        ShowSnackbar(
                            CommonSnackbarData(
                                message = CommonSnackbarMessage.InvalidState
                            )
                        )
                    )
                    return
                }

                scope.emitMutation(
                    SetToSettingForm(
                        currentUiState.gridSize,
                        currentUiState.difficulty
                    )
                )
            }

            FifteenPuzzleSampleAction.RetryGameWithSameSetting -> {
                val currentUiState = scope.getUiStateSnapshot()
                if (currentUiState !is FifteenPuzzleSampleUiState.GameCleared) {
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
        }
    }
}