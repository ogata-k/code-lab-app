package com.ogata_k.mobile.code_lab.ui.widget.button

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.ogata_k.mobile.code_lab.ui.theme.BoarderThickness
import com.ogata_k.mobile.code_lab.ui.theme.SpacingM
import com.ogata_k.mobile.code_lab.ui.theme.SpacingXS
import com.ogata_k.mobile.code_lab.ui.theme.SpacingXXS
import com.ogata_k.mobile.code_lab.ui.widget.text.ButtonMediumText
import com.ogata_k.mobile.code_lab.ui.widget.text.LabelMediumText

@Composable
fun <T> PullDown(
    modifier: Modifier = Modifier,
    items: Iterable<T>,
    current: T,
    toMenuLabel: @Composable (T) -> String,
    toButtonLabel: @Composable (T) -> String = toMenuLabel,
    /**
     * メニュー表示を閉じることができるならtrueを返す
     */
    selectMenu: (T) -> Boolean,
    buttonItem: @Composable (T, onClick: () -> Unit) -> Unit = { current, onClick ->
        OutlinedButton(
            onClick = onClick,
            colors = ButtonDefaults.filledTonalButtonColors(),
            contentPadding = PaddingValues(
                start = SpacingM,
                top = SpacingXXS,
                // 末尾にはアイコンが来るのでその分だけ余白を少なめにしておく
                end = SpacingXS,
                bottom = SpacingXXS,
            )
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(SpacingXS)
            ) {
                ButtonMediumText(toButtonLabel(current))
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = null
                )
            }
        }
    }
) {
    // ドロップダウンメニューを開いているかどうかは表示しているUIが作り直されない範囲で知っていればいいのでrememberを利用。
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = modifier) {
        buttonItem(current, { expanded = true })
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            items.forEachIndexed { index, item ->
                if (index == 0) {
                    HorizontalDivider(
                        thickness = BoarderThickness,
                        color = MaterialTheme.colorScheme.outlineVariant
                    )
                }

                val isSelected = item == current
                DropdownMenuItem(
                    text = {
                        LabelMediumText(
                            text = toMenuLabel(item),
                            color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Unspecified,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    },
                    colors = MenuDefaults.itemColors(
                        textColor = MaterialTheme.colorScheme.onSurface,
                        leadingIconColor = MaterialTheme.colorScheme.onSurface,
                        trailingIconColor = MaterialTheme.colorScheme.onSurface,
                        disabledTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    modifier = Modifier.background(
                        if (isSelected) {
                            MaterialTheme.colorScheme.secondaryContainer
                        } else {
                            Color.Transparent
                        }
                    ),
                    onClick = {
                        val canClose = selectMenu(item)
                        if (canClose) {
                            expanded = false
                        }
                    }
                )

                HorizontalDivider(
                    thickness = BoarderThickness,
                    color = MaterialTheme.colorScheme.outlineVariant
                )
            }
        }
    }
}