package com.ogata_k.mobile.code_lab.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.VerticalDragHandle
import androidx.compose.material3.VerticalDragHandleDefaults
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfoV2
import androidx.compose.material3.adaptive.layout.PaneExpansionAnchor
import androidx.compose.material3.adaptive.layout.rememberPaneExpansionState
import androidx.compose.material3.adaptive.navigation.BackNavigationBehavior
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
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.scene.SinglePaneSceneStrategy
import androidx.navigation3.ui.NavDisplay
import com.ogata_k.mobile.code_lab.R
import com.ogata_k.mobile.code_lab.common.logI
import com.ogata_k.mobile.code_lab.domain.enum.TemplateDiv
import com.ogata_k.mobile.code_lab.feature.counter_sample.CounterSampleRoute
import com.ogata_k.mobile.code_lab.feature.sample_template.SampleTemplateRoute
import com.ogata_k.mobile.code_lab.feature.select_template.SelectTemplateRoute
import com.ogata_k.mobile.code_lab.ui.theme.SpacingS
import com.ogata_k.mobile.code_lab.ui.theme.SpacingXXS
import com.ogata_k.mobile.code_lab.ui.widget.screen.BasicScaffold
import com.ogata_k.mobile.code_lab.ui.widget.text.BodyLargeText
import kotlinx.serialization.Serializable

sealed interface RouteNavKey : NavKey {
    /**
     * テンプレート選択のルーティング
     * 実質、ホーム画面
     */
    @Serializable
    data object SelectTemplate : RouteNavKey

    /**
     * アプリでよく見るサンプルテンプレートのルーティング
     */
    @Serializable
    data object SampleTemplate : RouteNavKey

    /**
     * カウンターサンプルのルーティング
     */
    @Serializable
    data object CounterSample : RouteNavKey

    fun isSelectTemplateDetail(): Boolean {
        return listOf(SampleTemplate).contains(this)
    }
}

sealed interface SceneKey {
    data object SelectTemplate : SceneKey
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
        // ドラッグ可能な境界点（アンカー）を定義する。
        // 一番近いアンカーにDragHandleが吸いつく挙動になる。
        anchors = listOf(
            // Listペインの最小サイズを 30% に制限
            PaneExpansionAnchor.Proportion(0.3f),

            // 40%
            PaneExpansionAnchor.Proportion(0.4f),

            // Listペインの最大サイズを 70% に制限（逆にDetailペインの最小サイズは 30%）
            PaneExpansionAnchor.Proportion(0.7f)
        )
    )
    val interactionSource = remember { MutableInteractionSource() }
    val listDetailStrategy = rememberListDetailSceneStrategy<NavKey>(
        backNavigationBehavior = BackNavigationBehavior.PopUntilCurrentDestinationChange,
        paneExpansionDragHandle = if (isSeparating) {
            null
        } else {
            { state ->
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(SpacingXXS),
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
        modifier = Modifier
            .background(MaterialTheme.colorScheme.onSurfaceVariant)
            .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Horizontal))
            .background(MaterialTheme.colorScheme.inverseOnSurface),
        backStack = backStack,
        onBack = {
            popLatest(backStack)
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
                    sceneKey = SceneKey.SelectTemplate,
                    detailPlaceholder = {
                        BasicScaffold(onBack = null) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center,
                            ) {
                                BodyLargeText(
                                    text = stringResource(R.string.placeholder_select_template),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                ),
            ) { _ ->
                SelectTemplateRoute(
                    navigateToTemplate = { templateDiv ->
                        when (templateDiv) {
                            TemplateDiv.Sample -> {
                                navigateToTemplateDetail(backStack, RouteNavKey.SampleTemplate)
                            }
                        }
                    }
                )
            }

            entry<RouteNavKey.SampleTemplate>(
                metadata = ListDetailSceneStrategy.detailPane(
                    sceneKey = SceneKey.SelectTemplate,
                ),
            ) { _ ->
                SampleTemplateRoute(
                    onBack = {
                        popLatest(backStack)
                    },
                    navigateToCounter = {
                        navigate(backStack, RouteNavKey.CounterSample)
                    }
                )
            }

            entry<RouteNavKey.CounterSample> { _ ->
                CounterSampleRoute(
                    onBack = {
                        popLatest(backStack)
                    }
                )
            }
        },
    )
}

/**
 * 指定したkeyに遷移
 */
private fun navigate(backStack: NavBackStack<NavKey>, key: RouteNavKey) {
    backStack.add(key)
    logI("Navigator") { "navigated: " + backStack.joinToString("->") { it.toString() } }
}

/**
 * テンプレート選択後の詳細画面への遷移メソッド
 */
private fun navigateToTemplateDetail(backStack: NavBackStack<NavKey>, key: RouteNavKey) {
    var popped = 0
    while (true) {
        val lastRoute = backStack.lastOrNull()
        if (lastRoute != null && lastRoute is RouteNavKey && lastRoute.isSelectTemplateDetail()) {
            if (lastRoute == key) {
                // すでに画面に乗っているので画面遷移処理の必要なし
                return
            }
            backStack.removeLastOrNull()
            popped += 1
            continue
        }
        break
    }

    navigate(backStack, key)
}

/**
 * 直近一つをスタックから取り除く
 */
private fun popLatest(backStack: NavBackStack<NavKey>) {
    backStack.removeLastOrNull()
    logI("Navigator") { "popped: " + backStack.joinToString("->") { it.toString() } }
}