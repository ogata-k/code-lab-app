package com.ogata_k.mobile.code_lab.feature.counter_sample

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ogata_k.mobile.code_lab.ui.widget.screen.AdaptiveRouteHost

/**
 * CounterSample featureのナビゲーションルートとなるComposable関数
 */
@Composable
fun CounterSampleRoute(
    viewModel: CounterSampleViewModel = hiltViewModel(),
    onBack: (() -> Unit)?,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    AdaptiveRouteHost(
        storeContainer = viewModel,
        buildDismissIntent = CounterSampleIntent::DismissDialog,
        onHandleUiEffect = { effect, snackbarHostState, context, scope ->
            // None
        },
    ) {
        CounterSampleScreen(
            uiState = uiState.featureUiState,
            onIntent = { viewModel.dispatchIntent(it) },
            onBack = onBack,
        )
    }
}