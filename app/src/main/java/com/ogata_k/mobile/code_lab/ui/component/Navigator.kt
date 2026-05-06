package com.ogata_k.mobile.code_lab.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.navigation3.ListDetailSceneStrategy
import androidx.compose.material3.adaptive.navigation3.rememberListDetailSceneStrategy
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.scene.SinglePaneSceneStrategy
import androidx.navigation3.ui.NavDisplay
import com.ogata_k.mobile.code_lab.R
import com.ogata_k.mobile.code_lab.feature.select_template.SelectTemplateRoute
import kotlinx.serialization.Serializable

sealed interface RouteNavKey : NavKey {
    @Serializable
    data object SelectTemplate : RouteNavKey
}

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun SetupRouting() {
    val backStack = rememberNavBackStack(RouteNavKey.SelectTemplate)
    val listDetailStrategy = rememberListDetailSceneStrategy<NavKey>()

    NavDisplay(
        backStack = backStack,
        onBack = {
            backStack.removeLastOrNull()
        },
        entryDecorators = listOf(
            // Entryを画面回転で破棄されないようにsaveableに保存させる
            rememberSaveableStateHolderNavEntryDecorator(),
            // Activity単位ではなくEntry単位でViewModelのライフサイクルを管理させる
            rememberViewModelStoreNavEntryDecorator(),
        ),
        sceneStrategies = listOf(
            listDetailStrategy,
            SinglePaneSceneStrategy(),
        ),
        entryProvider = entryProvider {
            entry<RouteNavKey.SelectTemplate>(
                metadata = ListDetailSceneStrategy.listPane(
                    sceneKey = "select-template",
                    detailPlaceholder = {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(MaterialTheme.colorScheme.surfaceContainerHighest),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = stringResource(R.string.placeholder_select_template),
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                )
            ) {
                SelectTemplateRoute()
            }
            // TODO setup detail metadata = ListDetailSceneStrategy.detailPane(sceneKey = "select-template")
        },
    )
}
