package com.ogata_k.mobile.code_lab.feature.fifteen_puzzle_sample

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import com.ogata_k.mobile.code_lab.R
import com.ogata_k.mobile.code_lab.domain.enum.FifteenPuzzleDifficulty
import com.ogata_k.mobile.code_lab.ui.theme.SpacingL
import com.ogata_k.mobile.code_lab.ui.theme.SpacingM
import com.ogata_k.mobile.code_lab.ui.theme.SpacingS
import com.ogata_k.mobile.code_lab.ui.theme.SpacingXXS
import com.ogata_k.mobile.code_lab.ui.theme.SpacingXXXS
import com.ogata_k.mobile.code_lab.ui.widget.button.PullDown
import com.ogata_k.mobile.code_lab.ui.widget.game.FifteenPuzzleGameBoard
import com.ogata_k.mobile.code_lab.ui.widget.screen.BasicScaffold
import com.ogata_k.mobile.code_lab.ui.widget.screen.effect.ConfettiScreen
import com.ogata_k.mobile.code_lab.ui.widget.text.BodyMediumText
import com.ogata_k.mobile.code_lab.ui.widget.text.ButtonMediumText
import com.ogata_k.mobile.code_lab.ui.widget.text.DisplayMediumText
import com.ogata_k.mobile.code_lab.ui.widget.text.HeadlineSmallText
import com.ogata_k.mobile.code_lab.ui.widget.text.TitleLargeText
import com.ogata_k.mobile.code_lab.ui.widget.text.TitleMediumText
import com.ogata_k.mobile.code_lab.ui.widget.text.TitleSmallText

/**
 * FifteenPuzzleSample featureのメイン画面を表示するComposable関数
 */
@Composable
fun FifteenPuzzleSampleScreen(
    uiState: FifteenPuzzleSampleUiState,
    isPlayClearAnimation: Boolean,
    onIntent: (FifteenPuzzleSampleIntent) -> Unit,
    onBack: (() -> Unit)?,
) {
    BasicScaffold(
        title = stringResource(R.string.screen_title_fifteen_puzzle),
        onBack = onBack,
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            when (uiState) {
                is FifteenPuzzleSampleUiState.NotStart -> {
                    NotStartBody(uiState, onIntent)
                }

                is FifteenPuzzleSampleUiState.Playing -> {
                    PlayingBody(uiState, isPlayClearAnimation, onIntent)
                }

                is FifteenPuzzleSampleUiState.GameCleared -> {
                    GameClearedBody(uiState, onIntent)
                }
            }
        }
    }
}

@Composable
fun NotStartBody(
    notStartUiState: FifteenPuzzleSampleUiState.NotStart,
    onIntent: (FifteenPuzzleSampleIntent) -> Unit
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(SpacingS),
        horizontalAlignment = Alignment.Start,
    ) {
        TitleMediumText(stringResource(R.string.start_message_for_fifteen_puzzle))
        BodyMediumText(stringResource(R.string.start_doc_for_fifteen_puzzle))

        Spacer(Modifier.height(SpacingM))

        TitleSmallText(stringResource(R.string.section_title_grid_size))
        Spacer(Modifier.height(SpacingXXXS))
        PullDown(
            items = listOf(3u, 4u, 5u, 6u),
            current = notStartUiState.gridSize,
            toMenuLabel = { stringResource(R.string.grid_size).format(it, it) },
            selectMenu = {
                onIntent(FifteenPuzzleSampleIntent.ChangeGridSize(it))
                return@PullDown true
            }
        )

        Spacer(Modifier.height(SpacingXXS))

        TitleSmallText(stringResource(R.string.section_title_difficulty))
        Spacer(Modifier.height(SpacingXXXS))
        PullDown(
            items = listOf(
                FifteenPuzzleDifficulty.Easy,
                FifteenPuzzleDifficulty.Normal,
                FifteenPuzzleDifficulty.Hard,
                FifteenPuzzleDifficulty.Extreme,
                FifteenPuzzleDifficulty.Nightmare,
            ),
            current = notStartUiState.difficulty,
            toMenuLabel = { it.toString() },
            selectMenu = {
                onIntent(FifteenPuzzleSampleIntent.ChangeDifficulty(it))
                return@PullDown true
            }
        )

        Spacer(Modifier.height(SpacingM))

        // ゲーム開始のためのボタン
        Box(
            modifier = Modifier.align(
                alignment = Alignment.CenterHorizontally
            )
        ) {
            Button(
                onClick = {
                    onIntent(FifteenPuzzleSampleIntent.ConfirmGameSettingBeforePlay)
                },
            ) {
                ButtonMediumText(stringResource(R.string.btn_confirm_setting))
            }
        }
    }
}

@Composable
fun PlayingBody(
    playingUiState: FifteenPuzzleSampleUiState.Playing,
    isPlayClearAnimation: Boolean,
    onIntent: (FifteenPuzzleSampleIntent) -> Unit
) {
    val scrollState = rememberScrollState()
    val scale by animateFloatAsState(
        targetValue = if (isPlayClearAnimation) 1.1f else 1.0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "BounceScale"
    )

    ConfettiScreen { confettiState, size ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(SpacingM, SpacingS, SpacingS, SpacingS),
            horizontalAlignment = Alignment.CenterHorizontally,
        )
        {
            Column(
                horizontalAlignment = Alignment.Start,
                modifier = Modifier.graphicsLayer(
                    scaleX = scale,
                    scaleY = scale
                )
            ) {
                TitleMediumText(
                    stringResource(R.string.step_counter_with_label).format(playingUiState.stepCount),
                    modifier = Modifier.align(Alignment.End),
                )

                Spacer(Modifier.height(SpacingXXXS))

                Box(contentAlignment = Alignment.Center) {
                    FifteenPuzzleGameBoard(playingUiState.board) {
                        onIntent(FifteenPuzzleSampleIntent.TapBoardCell(it))
                    }

                    if (isPlayClearAnimation) {
                        val density = LocalDensity.current
                        LaunchedEffect(Unit) {
                            val widthPx = with(density) { size.width.toPx() }
                            // 全体に雪みたいに散らせるが、下は少し開けた状態で紙吹雪を表示する。
                            val heightPx = with(density) { (size.height / 3 * 2).toPx() }
                            confettiState.snow(
                                width = widthPx,
                                sourceHeight = heightPx,
                                durationMillis = 1500L,
                            )
                        }

                        TitleLargeText(
                            text = stringResource(R.string.congratulations),
                            color = Color.Magenta,
                            modifier = Modifier.graphicsLayer(scaleX = 1.5f, scaleY = 1.5f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun GameClearedBody(
    gameClearedUiState: FifteenPuzzleSampleUiState.GameCleared,
    onIntent: (FifteenPuzzleSampleIntent) -> Unit
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(SpacingS),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        HeadlineSmallText(
            text = stringResource(R.string.congratulations),
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(Modifier.height(SpacingM))

        ElevatedCard(
            modifier = Modifier.padding(SpacingS)
        ) {
            Column(
                modifier = Modifier.padding(SpacingM),
                horizontalAlignment = Alignment.Start,
            ) {
                HeadlineSmallText(
                    text = stringResource(R.string.fixed_score),
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
                DisplayMediumText(
                    text = gameClearedUiState.score.toString(),
                    color = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )

                Spacer(Modifier.height(SpacingS))

                BodyMediumText(
                    text = stringResource(R.string.grid_size_with_label).format(
                        gameClearedUiState.gridSize.toInt(),
                        gameClearedUiState.gridSize.toInt()
                    )
                )
                BodyMediumText(
                    text = stringResource(R.string.difficulty_with_label).format(
                        gameClearedUiState.difficulty.toString()
                    )
                )
                BodyMediumText(
                    text = stringResource(R.string.estimate_difficulty_with_label).format(
                        gameClearedUiState.estimateBoardDifficulty.toInt()
                    )
                )
                BodyMediumText(
                    text = stringResource(R.string.estimate_step_count_with_label).format(
                        gameClearedUiState.estimateStepCount.toInt()
                    )
                )
                BodyMediumText(
                    text = stringResource(R.string.step_counter_with_label).format(
                        gameClearedUiState.stepCount.toInt()
                    )
                )
            }
        }

        Spacer(Modifier.height(SpacingL))

        Button(
            onClick = {
                onIntent(FifteenPuzzleSampleIntent.TapButtonToNextGame)
            }
        ) {
            ButtonMediumText(stringResource(R.string.btn_navigate_to_next_game))
        }
    }
}