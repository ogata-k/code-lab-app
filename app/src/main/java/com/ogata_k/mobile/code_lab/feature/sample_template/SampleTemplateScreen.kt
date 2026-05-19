package com.ogata_k.mobile.code_lab.feature.sample_template

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.ogata_k.mobile.code_lab.R
import com.ogata_k.mobile.code_lab.domain.enum.TemplateDiv
import com.ogata_k.mobile.code_lab.ui.theme.SpacingS
import com.ogata_k.mobile.code_lab.ui.theme.SpacingXS
import com.ogata_k.mobile.code_lab.ui.widget.list.item.BasicNavigationCardItem
import com.ogata_k.mobile.code_lab.ui.widget.screen.BasicTemplateDetailScaffold

/**
 * SampleTemplate featureのメイン画面を表示するComposable関数
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SampleTemplateScreen(
    uiState: SampleTemplateUiState,
    onIntent: (SampleTemplateIntent) -> Unit,
    onBack: (() -> Unit)?,
) {
    BasicTemplateDetailScaffold(
        title = TemplateDiv.Sample.name,
        onBack = onBack,
    ) {
        val scrollState = rememberScrollState()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(SpacingS),
            verticalArrangement = Arrangement.spacedBy(SpacingXS),
        ) {
            BasicNavigationCardItem(
                itemName = stringResource(R.string.counter),
                navigate = {
                    onIntent(SampleTemplateIntent.TapListItem)
                },
            )
        }
    }
}