package com.ogata_k.mobile.code_lab.feature.sample_template

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ogata_k.mobile.code_lab.ui.widget.screen.AdaptiveRouteHost

/**
 * SampleTemplate featureのナビゲーションルートとなるComposable関数
 */
@Composable
fun SampleTemplateRoute(
    viewModel: SampleTemplateViewModel = hiltViewModel(),
    onBack: (() -> Unit)?,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    AdaptiveRouteHost(
        storeContainer = viewModel,
        onHandleUiEffect = { effect, snackbarHostState, context, scope ->
            // TODO: Handle effect
        },
    ) {
        SampleTemplateScreen(
            uiState = uiState.featureUiState,
            onIntent = { viewModel.dispatchIntent(it) },
            onBack = onBack,
        )
    }
}