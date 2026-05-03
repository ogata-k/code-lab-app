package com.ogata_k.mobile.code_lab.feature.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.ogata_k.mobile.code_lab.R
import com.ogata_k.mobile.code_lab.common.BuildConfig
import com.ogata_k.mobile.code_lab.ui.theme.CodeLabTheme
import com.ogata_k.mobile.code_lab.ui.widget.screen.ScreenContainer

/**
 * Home featureのメイン画面を表示するComposable関数
 */
@Composable
fun HomeScreen(
    uiState: HomeUiState,
    onIntent: (HomeIntent) -> Unit,
    snackbarHostState: SnackbarHostState,
) {
    ScreenContainer {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            snackbarHost = { SnackbarHost(snackbarHostState) }
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