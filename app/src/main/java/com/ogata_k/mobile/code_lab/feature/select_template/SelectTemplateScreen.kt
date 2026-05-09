package com.ogata_k.mobile.code_lab.feature.select_template

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.ogata_k.mobile.code_lab.R
import com.ogata_k.mobile.code_lab.common.BuildConfig
import com.ogata_k.mobile.code_lab.ui.theme.CodeLabTheme

/**
 * SelectTemplate featureのメイン画面を表示するComposable関数
 */
@OptIn(ExperimentalMaterial3AdaptiveApi::class, ExperimentalMaterial3Api::class)
@Composable
fun SelectTemplateScreen(
    uiState: SelectTemplateUiState,
    onIntent: (SelectTemplateIntent) -> Unit,
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ), title = {
                    Text(stringResource(R.string.app_name))
                })
        },
    ) { innerPadding ->
        Box(
            Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Greeting(
                name = uiState.toString(),
            )
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    val appName: String = stringResource(R.string.app_name)
    Text(
        text = "Hello $name for $appName on ${
            if (BuildConfig.DEBUG) {
                "DEBUG"
            } else {
                "RELEASE"
            }
        }!",
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