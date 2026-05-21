package com.ogata_k.mobile.code_lab.feature.fifteen_puzzle_sample

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.ogata_k.mobile.code_lab.R
import com.ogata_k.mobile.code_lab.ui.theme.CodeLabTheme
import com.ogata_k.mobile.code_lab.ui.widget.screen.BasicScaffold
import com.ogata_k.mobile.code_lab.ui.widget.text.BodyLargeText

/**
 * FifteenPuzzleSample featureのメイン画面を表示するComposable関数
 */
@Composable
fun FifteenPuzzleSampleScreen(
    uiState: FifteenPuzzleSampleUiState,
    onIntent: (FifteenPuzzleSampleIntent) -> Unit,
    onBack: (() -> Unit)?,
) {
    BasicScaffold(
        title = stringResource(R.string.fifteen_puzzle_screen_title),
        onBack = onBack,
    ) {
        Greeting(
            name = uiState.toString(),
        )
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    val appName: String = stringResource(R.string.app_name)
    BodyLargeText(
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