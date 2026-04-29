package com.ogata_k.mobile.code_lab.feature.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.flow.collectLatest

/**
 * Home のナビゲーションルートとなるComposable関数
 */
@Composable
fun HomeRoute(
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        TODO("launch viewmodel initial loading")
    }

    LaunchedEffect(viewModel.uiEffect) {
        viewModel.uiEffect.collectLatest { effect ->
            // @todo Handle effect
        }
    }

    HomeScreen(
        uiState = uiState,
        onIntent = { viewModel.dispatch(it) }
    )
}