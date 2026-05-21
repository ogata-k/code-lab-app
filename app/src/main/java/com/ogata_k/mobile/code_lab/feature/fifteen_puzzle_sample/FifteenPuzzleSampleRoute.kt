package com.ogata_k.mobile.code_lab.feature.fifteen_puzzle_sample

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ogata_k.mobile.code_lab.ui.widget.screen.AdaptiveRouteHost

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
        onHandleUiEffect = { effect, snackbarHostState, context, scope ->
            // TODO: Handle effect
        },
    ) {
        FifteenPuzzleSampleScreen(
            uiState = uiState.featureUiState,
            onIntent = { viewModel.dispatchIntent(it) },
            onBack = onBack,
        )
    }
}