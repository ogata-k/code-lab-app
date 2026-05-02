package com.ogata_k.mobile.code_lab

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.res.stringResource
import com.ogata_k.mobile.code_lab.common.global_ui.GlobalUiController
import com.ogata_k.mobile.code_lab.common.global_ui.GlobalUiEffect
import com.ogata_k.mobile.code_lab.feature.home.HomeRoute
import com.ogata_k.mobile.code_lab.ui.theme.CodeLabTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var globalUiController: GlobalUiController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CodeLabTheme {
                AppMain(globalUiController = globalUiController) {
                    HomeRoute()
                }
            }
        }
    }
}

@Composable
fun AppMain(
    globalUiController: GlobalUiController,
    content: @Composable (PaddingValues) -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }
    // ダイアログをリスト（キュー）で保持
    val dialogQueue = remember { mutableStateListOf<GlobalUiEffect>() }

    LaunchedEffect(globalUiController) {
        globalUiController.effects.collect { effect ->
            when (effect) {
                is GlobalUiEffect.ShowSnackbar -> {
                    val result = snackbarHostState.showSnackbar(
                        message = effect.message,
                        actionLabel = effect.actionLabel,
                        duration = SnackbarDuration.Short,
                        withDismissAction = true
                    )
                    if (result == SnackbarResult.ActionPerformed) {
                        effect.onAction?.invoke()
                    }
                }

                is GlobalUiEffect.ShowErrorDialog,
                is GlobalUiEffect.ShowConfirmDialog,
                is GlobalUiEffect.ShowRequestActionDialog,
                    -> {
                    // ダイアログをキューに追加
                    dialogQueue.add(effect)
                }
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        content(innerPadding)

        // キューの先頭にあるダイアログを表示
        dialogQueue.firstOrNull()?.let { effect ->
            when (effect) {
                is GlobalUiEffect.ShowSnackbar -> {
                    // ダイアログ用ではないので処理の必要はなし
                }

                is GlobalUiEffect.ShowErrorDialog -> {
                    AlertDialog(
                        onDismissRequest = {
                            effect.onDismiss.invoke()
                            dialogQueue.remove(effect)
                        },
                        title = { Text(stringResource(R.string.dialog_title_error)) },
                        text = { Text(effect.message) },
                        confirmButton = {
                            TextButton(onClick = {
                                effect.onDismiss.invoke()
                                dialogQueue.remove(effect)
                            }) {
                                Text(stringResource(R.string.btn_close))
                            }
                        }
                    )
                }

                is GlobalUiEffect.ShowConfirmDialog -> {
                    AlertDialog(
                        onDismissRequest = {
                            effect.onDismiss.invoke()
                            dialogQueue.remove(effect)
                        },
                        title = { Text(stringResource(R.string.dialog_title_confirm)) },
                        text = { Text(effect.message) },
                        confirmButton = {
                            TextButton(onClick = {
                                effect.onDismiss()
                                dialogQueue.remove(effect)
                            }) {
                                Text(stringResource(R.string.btn_close))
                            }
                        }
                    )
                }

                is GlobalUiEffect.ShowRequestActionDialog -> {
                    AlertDialog(
                        onDismissRequest = {
                            effect.onDismiss.invoke()
                            dialogQueue.remove(effect)
                        },
                        title = { Text(effect.title) },
                        text = { Text(effect.message) },
                        dismissButton = {
                            TextButton(onClick = {
                                effect.onDismiss()
                                dialogQueue.remove(effect)
                            }) {
                                Text(effect.cancelButtonText ?: stringResource(R.string.btn_cancel))
                            }
                        },
                        confirmButton = {
                            TextButton(onClick = {
                                effect.onAction()
                                dialogQueue.remove(effect)
                            }) {
                                Text(effect.actionButtonText)
                            }
                        }
                    )
                }
            }
        }
    }
}
