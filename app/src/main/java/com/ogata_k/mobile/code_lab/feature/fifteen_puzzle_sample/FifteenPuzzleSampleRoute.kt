package com.ogata_k.mobile.code_lab.feature.fifteen_puzzle_sample

import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ogata_k.mobile.code_lab.R
import com.ogata_k.mobile.code_lab.ui.widget.screen.AdaptiveRouteHost
import kotlinx.coroutines.launch

/**
 * FifteenPuzzleSample featureのナビゲーションルートとなるComposable関数
 */
@Composable
fun FifteenPuzzleSampleRoute(
    viewModel: FifteenPuzzleSampleViewModel = hiltViewModel(),
    onBack: (() -> Unit)?,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    AdaptiveRouteHost(
        storeContainer = viewModel,
        buildDismissIntent = FifteenPuzzleSampleIntent::DismissDialog,
        onHandleUiEffect = { effect, snackbarHostState, context, scope ->
            when (effect) {
                FifteenPuzzleSampleUiEffect.ShowDifficultyMismatchError -> {
                    scope.launch {
                        val snackbarResult: SnackbarResult = snackbarHostState.showSnackbar(
                            message = context.getString(R.string.snackbar_difficulty_mismatch),
                            actionLabel = context.getString(R.string.snackbar_label_retry),
                            withDismissAction = true,
                        )
                        when (snackbarResult) {
                            SnackbarResult.Dismissed -> {
                                // none
                            }

                            SnackbarResult.ActionPerformed -> {
                                viewModel.dispatchIntent(FifteenPuzzleSampleIntent.RetryPlayGameByInvalidDifficulty)
                            }
                        }
                    }
                }
            }
        },
    ) {
        FifteenPuzzleSampleScreen(
            uiState = uiState.featureUiState,
            onIntent = { viewModel.dispatchIntent(it) },
            onBack = onBack,
        )
    }
}