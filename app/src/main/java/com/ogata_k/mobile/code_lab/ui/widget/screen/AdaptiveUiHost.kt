package com.ogata_k.mobile.code_lab.ui.widget.screen

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import com.ogata_k.mobile.code_lab.core.mvi.CommonUiEffect
import com.ogata_k.mobile.code_lab.core.mvi.StoreContainer
import com.ogata_k.mobile.code_lab.core.mvi.UiEffect
import com.ogata_k.mobile.code_lab.ui.widget.dialog.CommonDialog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun <T> AdaptiveUiHost(
    dialogQueue: List<T>,
    onDismissDialog: (T) -> Unit,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier,
    dialogContent: @Composable (T, onDismiss: () -> Unit) -> Unit,
    content: @Composable () -> Unit
) {
    Box(modifier = modifier.fillMaxSize()) {
        content()

        // スナックバー配置
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
        )

        // ダイアログの直列表示 (キューの先頭のみ表示)
        dialogQueue.firstOrNull()?.let { effect ->
            // 背景の暗転
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.6f))
                    .pointerInput(Unit) {}, // 下層への入力をブロック
            ) {
                dialogContent(effect) {
                    onDismissDialog(effect)
                }
            }
        }
    }
}

/**
 * 各Route（機能の入り口）で共通して必要になる、
 * Effectの収集、ダイアログのキュー管理、およびAdaptiveなレイアウトの器を提供する。
 */
@Composable
fun <UE : UiEffect> AdaptiveRouteHost(
    storeContainer: StoreContainer<*, UE, *, *>,
    onHandleCommonUiEffect: suspend (CommonUiEffect, SnackbarHostState, Context, CoroutineScope) -> Unit = { effect, snackbarHostState, context, coroutineScope ->
        when (effect) {
            is CommonUiEffect.ShowSnackbar -> {
                val data = effect.data
                val message = data.message.asString(context)
                val label = data.actionLabel?.asString(context)
                coroutineScope.launch {
                    snackbarHostState.showSnackbar(
                        message = message,
                        actionLabel = label,
                        withDismissAction = data.actionLabel != null,
                    )
                    data.onAction?.invoke()
                }
            }
        }
    },
    onHandleUiEffect: suspend (UE, SnackbarHostState, Context, CoroutineScope) -> Unit,
    screenContent: @Composable (SnackbarHostState) -> Unit
) {
    val context = LocalContext.current
    val lifecycle = LocalLifecycleOwner.current
    val snackbarHostState = remember { SnackbarHostState() }

    // uiStateを監視することで、ダイアログキューの変更を検知可能にする
    val screenState by storeContainer.uiState.collectAsState()

    // 共通のEffectをハンドルするロジック
    LaunchedEffect(storeContainer.commonUiEffect, lifecycle) {
        lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            val coroutineScope = this
            storeContainer.commonUiEffect.collect { effect ->
                onHandleCommonUiEffect(effect, snackbarHostState, context, coroutineScope)
            }
        }
    }

    LaunchedEffect(storeContainer.uiEffect, lifecycle) {
        lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            val coroutineScope = this
            storeContainer.uiEffect.collect { effect ->
                onHandleUiEffect(effect, snackbarHostState, context, coroutineScope)
            }
        }
    }

    // AdaptiveUiHost（ダイアログキュー管理込み）でのラップ
    AdaptiveUiHost(
        dialogQueue = screenState.localDialogQueue,
        onDismissDialog = { storeContainer.removeLocalDialog(it) },
        snackbarHostState = snackbarHostState,
        dialogContent = { effect, onDismiss ->
            CommonDialog(
                effect = effect,
                onDismiss = onDismiss
            )
        },
    ) {
        screenContent(snackbarHostState)
    }
}
