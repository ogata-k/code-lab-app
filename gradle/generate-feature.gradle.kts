tasks.register("generateFeature") {
    group = "generation"
    description =
        "新しい機能のボイラープレートを生成します。用法: ./gradlew generateFeature -PfeatureName=feature_name"
    doLast {
        // Gradleの組み込みプロパティ 'name' との衝突を避けるため 'featureName' を使用
        val inputName = project.findProperty("featureName") as? String
            ?: throw GradleException("機能名を指定してください。例: ./gradlew generateFeature -PfeatureName=feature_name")

        // 引数で渡された名前をそのままパッケージ/ディレクトリ用に使用
        val packageLabel = inputName.lowercase()
        // スネークケースやケバブケースをパスカルケース（クラス名用）に変換
        val featureName = inputName.split("_", "-")
            .joinToString("") { it.replaceFirstChar { char -> char.uppercase() } }

        val packageName = "com.ogata_k.mobile.code_lab.feature.$packageLabel"
        val targetDir = file("app/src/main/java/com/ogata_k/mobile/code_lab/feature/$packageLabel")

        if (!targetDir.exists()) {
            targetDir.mkdirs()
        }

        val templates = mapOf(
            "${featureName}UiState.kt" to """
                package $packageName
                import com.ogata_k.mobile.code_lab.feature.UiState
                
                /**
                 * $featureName のUI状態
                 */
                sealed interface ${featureName}UiState : UiState {
                    data object UnInitialized : ${featureName}UiState
                }
            """.trimIndent(),

            "${featureName}UiEffect.kt" to """
                package $packageName
                import com.ogata_k.mobile.code_lab.feature.UiEffect
                
                /**
                 * $featureName のUI副作用（ワンショットのイベント）
                 */
                sealed interface ${featureName}UiEffect : UiEffect
            """.trimIndent(),

            "${featureName}Action.kt" to """
                package $packageName
                import com.ogata_k.mobile.code_lab.feature.Action
                
                /**
                 * $featureName の内部で処理されるアクション
                 */
                sealed interface ${featureName}Action : Action {
                    data object Initialize : ${featureName}Action
                }
            """.trimIndent(),

            "${featureName}Intent.kt" to """
                package $packageName
                import com.ogata_k.mobile.code_lab.feature.Intent
                
                /**
                 * $featureName に対するユーザーの意図（操作）
                 */
                sealed interface ${featureName}Intent : Intent<${featureName}Action>
            """.trimIndent(),

            "${featureName}Mutation.kt" to """
                package $packageName
                import com.ogata_k.mobile.code_lab.feature.Mutation
                
                /**
                 * $featureName の状態を変更するための変更内容
                 */
                sealed interface ${featureName}Mutation : Mutation
            """.trimIndent(),

            "${featureName}ActionProcessor.kt" to """
                package $packageName
                import com.ogata_k.mobile.code_lab.feature.ActionProcessor
                import com.ogata_k.mobile.code_lab.feature.StateManagerScope
                import javax.inject.Inject
                
                /**
                 * $featureName のアクションを処理し、ミューテーションを生成するクラス
                 */
                class ${featureName}ActionProcessor @Inject constructor() : ActionProcessor<${featureName}UiState, ${featureName}UiEffect, ${featureName}Action, ${featureName}Mutation> {
                    override suspend fun process(
                        action: ${featureName}Action,
                        scope: StateManagerScope<${featureName}UiState, ${featureName}UiEffect, ${featureName}Mutation>
                    ) {
                        when (action) {
                            is ${featureName}Action.Initialize -> {
                                // TODO: 初期化処理
                            }
                        }
                    }
                }
            """.trimIndent(),

            "${featureName}Reducer.kt" to """
                package $packageName
                import com.ogata_k.mobile.code_lab.feature.Reducer
                
                /**
                 * $featureName の現在の状態とミューテーションから新しい状態を生成するクラス
                 */
                class ${featureName}Reducer : Reducer<${featureName}UiState, ${featureName}Mutation> {
                    override fun reduce(
                        currentState: ${featureName}UiState,
                        mutation: ${featureName}Mutation
                    ): ${featureName}UiState {
                        return currentState
                    }
                }
            """.trimIndent(),

            "${featureName}StateManager.kt" to """
                package $packageName
                import com.ogata_k.mobile.code_lab.feature.BaseStateManager
                import javax.inject.Inject
                
                /**
                 * $featureName の状態管理を統括するクラス
                 */
                class ${featureName}StateManager @Inject constructor(
                    processor: ${featureName}ActionProcessor,
                    reducer: ${featureName}Reducer
                ) : BaseStateManager<${featureName}UiState, ${featureName}UiEffect, ${featureName}Intent, ${featureName}Action, ${featureName}Mutation>(
                    initialState = ${featureName}UiState.UnInitialized,
                    actionProcessor = processor,
                    reducer = reducer
                )
            """.trimIndent(),

            "${featureName}ViewModel.kt" to """
                package $packageName
                import com.ogata_k.mobile.code_lab.feature.BaseViewModel
                import dagger.hilt.android.lifecycle.HiltViewModel
                import javax.inject.Inject
                
                /**
                 * $featureName の ViewModel
                 */
                @HiltViewModel
                class ${featureName}ViewModel @Inject constructor(
                    stateManager: ${featureName}StateManager
                ) : BaseViewModel<${featureName}UiState, ${featureName}UiEffect, ${featureName}Intent, ${featureName}Action, ${featureName}Mutation>(
                    stateManager = stateManager
                ) {
                    init {
                        // 初期データのロード
                        dispatchAction(${featureName}Action.Initialize)
                    }
                }
            """.trimIndent(),

            "${featureName}Route.kt" to """
                package $packageName
                import androidx.compose.runtime.Composable
                import androidx.compose.runtime.LaunchedEffect
                import androidx.compose.runtime.getValue
                import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
                import androidx.lifecycle.compose.collectAsStateWithLifecycle
                import kotlinx.coroutines.flow.collectLatest
                
                /**
                 * $featureName のナビゲーションルートとなるComposable関数
                 */
                @Composable
                fun ${featureName}Route(
                    viewModel: ${featureName}ViewModel = hiltViewModel()
                ) {
                    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
                    
                    LaunchedEffect(viewModel.uiEffect) {
                        viewModel.uiEffect.collectLatest { effect ->
                            // TODO: Handle effect
                        }
                    }
                    
                    ${featureName}Screen(
                        uiState = uiState,
                        onIntent = { viewModel.dispatchIntent(it) }
                    )
                }
            """.trimIndent(),

            "${featureName}Screen.kt" to """
                package $packageName
                import androidx.compose.runtime.Composable
                import androidx.compose.ui.Modifier
                
                /**
                 * $featureName のメイン画面を表示するComposable関数
                 */
                @Composable
                fun ${featureName}Screen(
                    uiState: ${featureName}UiState,
                    onIntent: (${featureName}Intent) -> Unit,
                    modifier: Modifier = Modifier
                ) {
                    // UI Implementation
                }
            """.trimIndent()
        )

        templates.forEach { (fileName, content) ->
            val file = targetDir.resolve(fileName)
            if (!file.exists()) {
                file.writeText(content)
                println("Generated: ${'$'}fileName")
            } else {
                println("Skipped (already exists): ${'$'}fileName")
            }
        }
    }
}
