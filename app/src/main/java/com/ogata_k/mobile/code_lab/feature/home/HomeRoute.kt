package com.ogata_k.mobile.code_lab.feature.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ogata_k.mobile.code_lab.ui.widget.screen.AdaptiveRouteHost

/**
 * Home featureのナビゲーションルートとなるComposable関数
 */
@Composable
fun HomeRoute(
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    AdaptiveRouteHost(
        storeContainer = viewModel,
        onHandleUiEffect = { effect, snackbarHostState, context, scope ->
            // TODO: 実際の処理
        },
    ) {
        HomeScreen(
            uiState = uiState.featureUiState,
            onIntent = { viewModel.dispatchIntent(it) },
        )
    }
}
