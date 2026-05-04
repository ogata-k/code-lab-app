package com.ogata_k.mobile.code_lab

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.ogata_k.mobile.code_lab.feature.home.HomeRoute
import com.ogata_k.mobile.code_lab.global.GlobalUiController
import com.ogata_k.mobile.code_lab.global.GlobalUiEffect
import com.ogata_k.mobile.code_lab.global.GlobalUiEffectLabel
import com.ogata_k.mobile.code_lab.global.GlobalUiEffectMessage
import com.ogata_k.mobile.code_lab.ui.theme.CodeLabTheme
import com.ogata_k.mobile.code_lab.ui.widget.dialog.BasicDialog
import com.ogata_k.mobile.code_lab.ui.widget.screen.AdaptiveUiHost
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
    val globalSnackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(globalUiController) {
        globalUiController.effects.collect { effect ->
            when (effect) {
                is GlobalUiEffect.ShowToast -> {
                    val duration = if (effect.showAsLong) {
                        Toast.LENGTH_LONG
                    } else {
                        Toast.LENGTH_SHORT
                    }
                    Toast.makeText(context, effect.message.asString(context), duration).show()
                }

                is GlobalUiEffect.ShowCriticalAlertDialog -> {
                    onAddDialog(effect)
                }

                is GlobalUiEffect.ShowCriticalAlertSnackbar -> {
                    launch {
                        globalSnackbarHostState.showSnackbar(
                            message = effect.message.asString(context),
                            actionLabel = effect.actionLabel?.asString(context),
                            withDismissAction = effect.actionLabel != null,
                        )
                        effect.onAction?.invoke()
                    }
                }
            }
        }
    }

    AdaptiveUiHost(
        dialogQueue = dialogQueue,
        onDismissDialog = onRemoveDialog,
        snackbarHostState = globalSnackbarHostState,
        dialogContent = { effect, onDismiss ->
            when (effect) {
                is GlobalUiEffect.ShowCriticalAlertDialog -> {
                    BasicDialog(
                        title = stringResource(R.string.dialog_title_error),
                        text = effect.message.asString(context),
                        dismissButtonText = stringResource(R.string.btn_close),
                        onDismissRequest = {
                            effect.onDismiss?.invoke()
                            onDismiss()
                        },
                    )
                }

                is GlobalUiEffect.ShowCriticalAlertSnackbar,
                is GlobalUiEffect.ShowToast,
                    -> {
                    // ダイアログではないものは他で処理しているので処理の必要なし
                }
            }
        }
    ) {
        content()
    }
}

private fun GlobalUiEffectMessage.asString(context: Context): String {
    return when (this) {
        is GlobalUiEffectMessage.UnexpectedError -> context.getString(R.string.error_unexpected)
    }
}

private fun GlobalUiEffectLabel.asString(context: Context): String {
    // TODO 実際の処理
    return "$this"
}