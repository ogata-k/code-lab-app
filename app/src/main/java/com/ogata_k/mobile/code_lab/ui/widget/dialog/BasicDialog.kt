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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.ogata_k.mobile.code_lab.ui.theme.SpacingM
import com.ogata_k.mobile.code_lab.ui.theme.SpacingS
import com.ogata_k.mobile.code_lab.ui.theme.SpacingXS
import com.ogata_k.mobile.code_lab.ui.theme.SpacingXXL

@Composable
fun BasicDialog(
    title: String,
    text: String,
    dismissButtonText: String,
    onDismissRequest: () -> Unit,
    action: Pair<String, (dismissDialog: () -> Unit) -> Unit>? = null,
    properties: DialogProperties = DialogProperties(
        dismissOnClickOutside = false,
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
        Surface(
            shape = RoundedCornerShape(SpacingS),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = SpacingXS
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .padding(SpacingM)
            ) {
                if (message != null) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator(modifier = Modifier.size(SpacingXXL))
                        Spacer(modifier = Modifier.size(SpacingS))
                        Text(text = message)
                    }
                } else {
                    CircularProgressIndicator(modifier = Modifier.size(SpacingXXL))
                }
            }
        }
    }
}
