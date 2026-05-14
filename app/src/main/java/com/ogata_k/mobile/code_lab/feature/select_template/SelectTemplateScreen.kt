package com.ogata_k.mobile.code_lab.feature.select_template

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.ogata_k.mobile.code_lab.R
import com.ogata_k.mobile.code_lab.domain.enum.TemplateDiv
import com.ogata_k.mobile.code_lab.ui.theme.SpacingXS
import com.ogata_k.mobile.code_lab.ui.widget.list.item.BasicNavigationCardItem
import com.ogata_k.mobile.code_lab.ui.widget.screen.BasicScaffold

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
            modifier = Modifier.padding(all = SpacingXS)
        ) {
            Column() {
                BasicNavigationCardItem(
                    itemName = TemplateDiv.Sample.name,
                    navigate = {
                        onIntent(SelectTemplateIntent.TapListItem(TemplateDiv.Sample))
                    },
                )
            }
        }
    }
}