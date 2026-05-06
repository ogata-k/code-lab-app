package com.ogata_k.mobile.code_lab.feature.select_template

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ogata_k.mobile.code_lab.ui.widget.screen.AdaptiveRouteHost

/**
 * SelectTemplate featureのナビゲーションルートとなるComposable関数
 */
@Composable
fun SelectTemplateRoute(
    viewModel: SelectTemplateViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    AdaptiveRouteHost(
        storeContainer = viewModel,
        onHandleUiEffect = { effect, snackbarHostState, context, scope ->
            // TODO: 実際の処理
        },
    ) {
        SelectTemplateScreen(
            uiState = uiState.featureUiState,
            onIntent = { viewModel.dispatchIntent(it) },
        )
    }
}
