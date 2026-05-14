package com.ogata_k.mobile.code_lab.ui.widget.list.item

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import com.ogata_k.mobile.code_lab.ui.theme.SpacingS
import com.ogata_k.mobile.code_lab.ui.widget.text.BodyLargeText

@Composable
fun BasicNavigationCardItem(itemName: String, navigate: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer,
        ),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    navigate()
                }
                .padding(all = SpacingS)) {
            Row {
                BodyLargeText(text = itemName)
                Spacer(modifier = Modifier.weight(1f))
                Icon(
                    painter = rememberVectorPainter(image = Icons.AutoMirrored.Filled.KeyboardArrowRight),
                    contentDescription = null,
                )
            }
        }
    }
}