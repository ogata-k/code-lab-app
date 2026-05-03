package com.ogata_k.mobile.code_lab

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.ogata_k.mobile.code_lab.common.global_ui.GlobalUiController
import com.ogata_k.mobile.code_lab.common.global_ui.GlobalUiEffect
import com.ogata_k.mobile.code_lab.feature.home.HomeRoute
import com.ogata_k.mobile.code_lab.ui.theme.CodeLabTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var globalUiController: GlobalUiController

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CodeLabTheme {
                AppMain(
                    globalUiController = globalUiController,
                    dialogQueue = viewModel.dialogQueue,
                    onAddDialog = viewModel::addDialog,
                    onRemoveDialog = viewModel::removeDialog
                ) {
                    HomeRoute()
                }
            }
        }
    }
}

@Composable
fun AppMain(
    globalUiController: GlobalUiController,
    dialogQueue: List<GlobalUiEffect>,
    onAddDialog: (GlobalUiEffect) -> Unit,
    onRemoveDialog: (GlobalUiEffect) -> Unit,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(globalUiController) {
        globalUiController.effects.collect { effect ->
            when (effect) {
                // SnackbarとFABの重なりには一応注意が必要。
                // AppMainでSnackbarを表示し、HomeScreenなど固有の画面にFloatingActionButton(FAB)を置いた場合、
                // SnackbarがFABの上に被さって表示される可能性がある。
                // （同じScaffold内であれば自動で避けてくれますが、別々のScaffoldだと位置の調整が行われないため。）
                is GlobalUiEffect.ShowSnackbar -> {
                    launch {
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
                }

                is GlobalUiEffect.ShowToast -> {
                    val duration = if (effect.showLong) {
                        Toast.LENGTH_LONG
                    } else {
                        Toast.LENGTH_SHORT
                    }
                    val toast = Toast.makeText(context, effect.message, duration)
                    toast.show()
                }

                is GlobalUiEffect.ShowErrorDialog,
                is GlobalUiEffect.ShowConfirmDialog,
                is GlobalUiEffect.ShowRequestActionDialog,
                    -> {
                    // ダイアログをキューに追加
                    onAddDialog(effect)
                }
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // メインUI
        content()

        // メインUIで消されないようにスナックバーを別途表示させる
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
        )

        // キューの先頭にあるダイアログを表示
        dialogQueue.firstOrNull()?.let { effect ->
            when (effect) {
                is GlobalUiEffect.ShowSnackbar,
                is GlobalUiEffect.ShowToast,
                    -> {
                    // ダイアログ用ではないので処理の必要はなし
                }

                is GlobalUiEffect.ShowErrorDialog -> {
                    AlertDialog(
                        onDismissRequest = {
                            effect.onDismiss.invoke()
                            onRemoveDialog(effect)
                        },
                        title = { Text(stringResource(R.string.dialog_title_error)) },
                        text = { Text(effect.message) },
                        confirmButton = {
                            TextButton(onClick = {
                                effect.onDismiss.invoke()
                                onRemoveDialog(effect)
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
                            onRemoveDialog(effect)
                        },
                        title = { Text(stringResource(R.string.dialog_title_confirm)) },
                        text = { Text(effect.message) },
                        confirmButton = {
                            TextButton(onClick = {
                                effect.onDismiss()
                                onRemoveDialog(effect)
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
                            onRemoveDialog(effect)
                        },
                        title = { Text(effect.title) },
                        text = { Text(effect.message) },
                        dismissButton = {
                            TextButton(onClick = {
                                effect.onDismiss()
                                onRemoveDialog(effect)
                            }) {
                                Text(effect.cancelButtonText ?: stringResource(R.string.btn_cancel))
                            }
                        },
                        confirmButton = {
                            TextButton(onClick = {
                                effect.onAction()
                                onRemoveDialog(effect)
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
