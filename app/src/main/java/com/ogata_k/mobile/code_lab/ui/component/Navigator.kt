package com.ogata_k.mobile.code_lab.ui.component

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.VerticalDragHandle
import androidx.compose.material3.VerticalDragHandleDefaults
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfoV2
import androidx.compose.material3.adaptive.layout.PaneExpansionAnchor
import androidx.compose.material3.adaptive.layout.rememberPaneExpansionState
import androidx.compose.material3.adaptive.navigation3.ListDetailSceneStrategy
import androidx.compose.material3.adaptive.navigation3.rememberListDetailSceneStrategy
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.scene.SinglePaneSceneStrategy
import androidx.navigation3.ui.NavDisplay
import com.ogata_k.mobile.code_lab.R
import com.ogata_k.mobile.code_lab.feature.select_template.SelectTemplateRoute
import com.ogata_k.mobile.code_lab.ui.theme.SpacingS
import com.ogata_k.mobile.code_lab.ui.theme.SpacingXS
import kotlinx.serialization.Serializable

sealed interface RouteNavKey : NavKey {
    @Serializable
    data object SelectTemplate : RouteNavKey
}

@OptIn(ExperimentalMaterial3AdaptiveApi::class, ExperimentalMaterial3Api::class)
@Composable
fun SetupRouting() {
    // ヒンジなどで画面が物理的に分かれているかどうかを判定する。
    // 物理的に分かれている場合は、分割部分をドラッグできないようにする。
    val adaptiveInfo = currentWindowAdaptiveInfoV2()
    val isSeparating = adaptiveInfo.windowPosture.hingeList.any { it.isSeparating }

    val backStack = rememberNavBackStack(RouteNavKey.SelectTemplate)

    val expansionState = rememberPaneExpansionState(
        // ドラッグ可能な境界点（アンカー）を定義する
        anchors = listOf(
            // Listペインの最小サイズを 300.dp に制限（これ以上左にドラッグ不可）
            // ※固定値で指定する場合。画面幅に応じて変えたい場合は Fraction(0.3f) などを使用
            PaneExpansionAnchor.Proportion(0.3f),

            // 中央 (50%)
            PaneExpansionAnchor.Proportion(0.5f),

            // Detailペインの最小サイズを確保するためのアンカー
            // 例: 右端から 400.dp の位置（これ以上右にドラッグ不可）
            // ※固定値で指定する場合。画面幅に応じて変えたい場合は Fraction(0.7f) などを使用
            PaneExpansionAnchor.Proportion(0.7f)
        )
    )
    val interactionSource = remember { MutableInteractionSource() }
    val listDetailStrategy = rememberListDetailSceneStrategy<NavKey>(
        paneExpansionDragHandle = if (isSeparating) {
            null
        } else {
            { state ->
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(SpacingXS),
                    contentAlignment = Alignment.Center
                ) {
                    VerticalDragHandle(
                        modifier = Modifier.paneExpansionDraggable(
                            state = state,
                            minTouchTargetSize = SpacingS,
                            interactionSource = interactionSource,
                            semanticsProperties = {
                                role = Role.Button
                            },
                        ),
                        colors = VerticalDragHandleDefaults.colors(
                            color = MaterialTheme.colorScheme.outlineVariant,
                            pressedColor = MaterialTheme.colorScheme.primary,
                            draggedColor = MaterialTheme.colorScheme.primary,
                        ),
                        interactionSource = interactionSource,
                    )
                }
            }
        },
        paneExpansionState = if (isSeparating) {
            null
        } else {
            expansionState
        },
    )

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
                        Scaffold(
                            modifier = Modifier.fillMaxSize(),
                            topBar = {
                                TopAppBar(
                                    colors = TopAppBarDefaults.topAppBarColors(
                                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                                        titleContentColor = MaterialTheme.colorScheme.primary,
                                    ), title = {
                                        // 一覧のヘッダーに高さを合わせるためにダミーとして指定
                                    })
                            },
                        ) { innerPadding ->
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(innerPadding),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = stringResource(R.string.placeholder_select_template),
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
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
