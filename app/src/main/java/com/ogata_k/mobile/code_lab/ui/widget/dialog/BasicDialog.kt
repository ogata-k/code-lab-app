package com.ogata_k.mobile.code_lab.ui.widget.dialog

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.window.DialogProperties

@Composable
fun BasicDialog(
    title: String,
    text: String,
    dismissButtonText: String,
    onDismissRequest: () -> Unit,
    action: Pair<String, (dismissDialog: () -> Unit) -> Unit>? = null,
    properties: DialogProperties = DialogProperties(
        dismissOnClickOutside = false,
        dismissOnBackPress = false,
    ),
) {
    AlertDialog(
        onDismissRequest = {
            // propertyによっては無くても同じだが、propertyを変更できるので念のため
            onDismissRequest()
        },
        properties = properties,
        title = { Text(title) },
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
