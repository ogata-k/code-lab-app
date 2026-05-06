package com.ogata_k.mobile.code_lab.ui.widget.dialog

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.DialogWindowProvider

@Composable
fun BasicDialog(
    title: String,
    text: String,
    dismissButtonText: String,
    onDismissRequest: () -> Unit,
    action: Pair<String, (dismissDialog: () -> Unit) -> Unit>? = null,
    properties: DialogProperties = DialogProperties(
        dismissOnClickOutside = true,
        dismissOnBackPress = true,
    ),
) {
    AlertDialog(
        onDismissRequest = {
            // propertyによっては無くても同じだが、propertyを変更できるので念のため
            onDismissRequest()
        },
        properties = properties,
        title = {
            val dialogWindow = (LocalView.current.parent as? DialogWindowProvider)?.window
            SideEffect {
                dialogWindow?.setDimAmount(0f)
            }
            Text(title)
        },
        text = { Text(text) },
        dismissButton = if (action == null) {
            null
        } else {
            {
                TextButton(onClick = {
                    onDismissRequest()
                }) {
                    Text(dismissButtonText)
                }
            }
        },
        confirmButton = if (action == null) {
            {
                TextButton(onClick = {
                    onDismissRequest()
                }) {
                    Text(dismissButtonText)
                }
            }
        } else {
            {
                TextButton(onClick = {
                    action.second(onDismissRequest)
                }) {
                    Text(action.first)
                }
            }
        },
    )
}

@Composable
fun LoadingDialog(
    message: String? = null,
) {
    Dialog(
        onDismissRequest = {},
        properties = DialogProperties(
            dismissOnBackPress = false,
            dismissOnClickOutside = false,
        )
    ) {
        val dialogWindow = (LocalView.current.parent as? DialogWindowProvider)?.window
        SideEffect {
            dialogWindow?.setDimAmount(0f)
        }

        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .padding(24.dp)
            ) {
                if (message != null) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator(modifier = Modifier.size(48.dp))
                        Spacer(modifier = Modifier.size(16.dp))
                        Text(text = message)
                    }
                } else {
                    CircularProgressIndicator(modifier = Modifier.size(48.dp))
                }
            }
        }
    }
}
