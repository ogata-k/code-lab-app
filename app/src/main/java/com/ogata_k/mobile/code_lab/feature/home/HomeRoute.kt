package com.ogata_k.mobile.code_lab.feature.home

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle

/**
 * Home featureのナビゲーションルートとなるComposable関数
 */
@Composable
fun HomeRoute(
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    val lifecycle = LocalLifecycleOwner.current

    LaunchedEffect(viewModel.uiEffect, lifecycle) {
        lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.uiEffect.collect { effect ->
                when (effect) {
                    // TODO: ちゃんとした処理にする
                    is HomeUiEffect.ShowInitializedSnackbar -> {
                        snackbarHostState.showSnackbar("初期化に成功しました")
                    }
                }
            }
        }
    }

    HomeScreen(
        uiState = uiState,
        onIntent = { viewModel.dispatchIntent(it) },
        snackbarHostState = snackbarHostState,
    )
}