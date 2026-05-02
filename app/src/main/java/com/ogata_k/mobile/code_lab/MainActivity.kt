package com.ogata_k.mobile.code_lab

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.ogata_k.mobile.code_lab.feature.home.HomeRoute
import com.ogata_k.mobile.code_lab.ui.theme.CodeLabTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CodeLabTheme {
                HomeRoute()
            }
        }
    }
}