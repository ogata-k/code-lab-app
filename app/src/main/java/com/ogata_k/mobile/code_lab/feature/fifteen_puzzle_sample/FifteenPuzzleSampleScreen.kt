package com.ogata_k.mobile.code_lab.feature.fifteen_puzzle_sample

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.ogata_k.mobile.code_lab.R
import com.ogata_k.mobile.code_lab.domain.enum.FifteenPuzzleDifficulty
import com.ogata_k.mobile.code_lab.ui.theme.SpacingM
import com.ogata_k.mobile.code_lab.ui.theme.SpacingS
import com.ogata_k.mobile.code_lab.ui.theme.SpacingXXS
import com.ogata_k.mobile.code_lab.ui.theme.SpacingXXXS
import com.ogata_k.mobile.code_lab.ui.widget.button.PullDown
import com.ogata_k.mobile.code_lab.ui.widget.screen.BasicScaffold
import com.ogata_k.mobile.code_lab.ui.widget.text.BodyMediumText
import com.ogata_k.mobile.code_lab.ui.widget.text.LabelMediumText
import com.ogata_k.mobile.code_lab.ui.widget.text.TitleMediumText
import com.ogata_k.mobile.code_lab.ui.widget.text.TitleSmallText

/**
 * FifteenPuzzleSample featureのメイン画面を表示するComposable関数
 */
@Composable
fun FifteenPuzzleSampleScreen(
    uiState: FifteenPuzzleSampleUiState,
    onIntent: (FifteenPuzzleSampleIntent) -> Unit,
    onBack: (() -> Unit)?,
) {
    BasicScaffold(
        title = stringResource(R.string.screen_title_fifteen_puzzle),
        onBack = onBack,
    ) {
        when (uiState) {
            is FifteenPuzzleSampleUiState.NotStart -> {
                NotStartBody(uiState, onIntent)
            }

            is FifteenPuzzleSampleUiState.Playing -> {
                PlayingBody(uiState, onIntent)
            }

            is FifteenPuzzleSampleUiState.GameCleared -> {
                GameClearedBody(uiState, onIntent)
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
            toMenuLabel = { "%s × %s".format(it, it) },
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
                LabelMediumText(stringResource(R.string.btn_confirm_setting))
            }
        }
    }
}

@Composable
fun PlayingBody(
    playingUiState: FifteenPuzzleSampleUiState.Playing,
    onIntent: (FifteenPuzzleSampleIntent) -> Unit
) {
    Text(playingUiState.toString())
    // TODO 実際の画面。注意として右下が最大値で空白にすることがゴールであることがわかるようなUIにすること
}

@Composable
fun GameClearedBody(
    gameClearedUiState: FifteenPuzzleSampleUiState.GameCleared,
    onIntent: (FifteenPuzzleSampleIntent) -> Unit
) {
    TODO("Not yet implemented")
}