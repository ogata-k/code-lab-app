package com.ogata_k.mobile.code_lab.feature.sample_template

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.ogata_k.mobile.code_lab.R
import com.ogata_k.mobile.code_lab.domain.enum.TemplateDiv
import com.ogata_k.mobile.code_lab.ui.theme.CodeLabTheme
import com.ogata_k.mobile.code_lab.ui.widget.screen.BasicScaffold

/**
 * SampleTemplate featureのメイン画面を表示するComposable関数
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SampleTemplateScreen(
    uiState: SampleTemplateUiState,
    onIntent: (SampleTemplateIntent) -> Unit,
) {
    BasicScaffold(
        title = TemplateDiv.Sample.name,
    ) {
        Greeting(
            name = uiState.toString(),
        )
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    val appName: String = stringResource(R.string.app_name)
    Text(
        text = "Hello $name for $appName!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    CodeLabTheme {
        Greeting("Android")
    }
}