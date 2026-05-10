package com.ogata_k.mobile.code_lab.feature.select_template

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ListItem
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.ogata_k.mobile.code_lab.R
import com.ogata_k.mobile.code_lab.domain.enum.TemplateDiv
import com.ogata_k.mobile.code_lab.ui.theme.NoSpacing
import com.ogata_k.mobile.code_lab.ui.theme.SpacingXS
import com.ogata_k.mobile.code_lab.ui.widget.screen.BasicScaffold
import com.ogata_k.mobile.code_lab.ui.widget.text.BodyLargeText

/**
 * SelectTemplate featureのメイン画面を表示するComposable関数
 */
@OptIn(ExperimentalMaterial3AdaptiveApi::class, ExperimentalMaterial3Api::class)
@Composable
fun SelectTemplateScreen(
    uiState: SelectTemplateUiState,
    onIntent: (SelectTemplateIntent) -> Unit,
) {
    BasicScaffold(
        title = stringResource(R.string.app_name),
        onBack = null,
    ) {
        Box(
            modifier = Modifier.padding(
                start = SpacingXS,
                top = SpacingXS,
                end = SpacingXS,
                bottom = NoSpacing
            )
        ) {
            Column() {
                ListItem(
                    modifier = Modifier.clickable {
                        onIntent(SelectTemplateIntent.NavigateToTemplate(TemplateDiv.Sample))
                    },
                    headlineContent = {
                        BodyLargeText(TemplateDiv.Sample.name)
                    }

                )
            }
        }
    }
}