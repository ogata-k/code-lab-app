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
                
                import com.ogata_k.mobile.code_lab.core.mvi.UiState
                
                /**
                 * ${featureName} featureのUI状態
                 */
                sealed interface ${featureName}UiState : UiState {
                    data object UnInitialized : ${featureName}UiState
                }
            """.trimIndent(),

            "${featureName}UiEffect.kt" to """
                package $packageName
                
                import com.ogata_k.mobile.code_lab.core.mvi.UiEffect
                
                /**
                 * ${featureName} featureのUI副作用（ワンショットのイベント）
                 */
                sealed interface ${featureName}UiEffect : UiEffect
            """.trimIndent(),

            "${featureName}Action.kt" to """
                package $packageName
                
                import com.ogata_k.mobile.code_lab.core.mvi.Action
                import com.ogata_k.mobile.code_lab.core.mvi.ExecutionStrategy
                
                /**
                 * ${featureName} featureの内部で処理されるアクション
                 */
                sealed interface ${featureName}Action : Action {
                    data object Initialize : ${featureName}Action {
                        override val strategy: ExecutionStrategy = ExecutionStrategy.Parallel
                    }
                }
            """.trimIndent(),

            "${featureName}Intent.kt" to """
                package $packageName
                
                import com.ogata_k.mobile.code_lab.core.mvi.Intent
                
                /**
                 * ${featureName} featureに対するユーザーの意図（操作）
                 */
                sealed interface ${featureName}Intent : Intent<${featureName}Action> {
                    override fun toAction(): ${featureName}Action? = when (this) {
                        // TODO: Intentが増えたらここに追加
                        else -> null
                    }
                }
            """.trimIndent(),

            "${featureName}Mutation.kt" to """
                package $packageName
                
                import com.ogata_k.mobile.code_lab.core.mvi.Mutation
                
                /**
                 * ${featureName} featureの状態を変更するための変更内容
                 */
                sealed interface ${featureName}Mutation : Mutation
            """.trimIndent(),

            "${featureName}ActionProcessor.kt" to """
                package $packageName
                
                import com.ogata_k.mobile.code_lab.core.mvi.ActionProcessor
                import com.ogata_k.mobile.code_lab.core.mvi.StateManagerScope
                import javax.inject.Inject
                
                /**
                 * ${featureName} featureのアクションを処理し、ミューテーションを生成するクラス
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
                
                import com.ogata_k.mobile.code_lab.core.mvi.Reducer
                
                /**
                 * ${featureName} featureの現在の状態とミューテーションから新しい状態を生成するクラス
                 */
                class ${featureName}Reducer : Reducer<${featureName}UiState, ${featureName}Mutation> {
                    override fun reduce(
                        currentState: ${featureName}UiState,
                        mutation: ${featureName}Mutation
                    ): ${featureName}UiState {
                        // TODO: 実際の変換処理
                        return currentState
                    }
                }
            """.trimIndent(),

            "${featureName}StateManager.kt" to """
                package $packageName
                
                import com.ogata_k.mobile.code_lab.core.mvi.BaseStateManager
                import kotlinx.coroutines.CoroutineScope
                
                /**
                 * ${featureName} featureの状態管理を統括するクラス
                 */
                class ${featureName}StateManager(
                    scope: CoroutineScope,
                    initialState: ${featureName}UiState,
                    actionProcessor: ${featureName}ActionProcessor,
                    reducer: ${featureName}Reducer
                ) : BaseStateManager<${featureName}UiState, ${featureName}UiEffect, ${featureName}Intent, ${featureName}Action, ${featureName}Mutation>(
                    scope = scope,
                    initialState = initialState,
                    actionProcessor = actionProcessor,
                    reducer = reducer
                )
            """.trimIndent(),

            "${featureName}ViewModel.kt" to """
                package $packageName
                
                import androidx.lifecycle.viewModelScope
                import com.ogata_k.mobile.code_lab.core.mvi.BaseViewModel
                import dagger.hilt.android.lifecycle.HiltViewModel
                import javax.inject.Inject
                
                /**
                 * ${featureName} featureのViewModel
                 */
                @HiltViewModel
                class ${featureName}ViewModel @Inject constructor(
                    actionProcessor: ${featureName}ActionProcessor,
                ) : BaseViewModel<${featureName}UiState, ${featureName}UiEffect, ${featureName}Intent, ${featureName}Action, ${featureName}Mutation>() {
                    override val stateManager: ${featureName}StateManager = ${featureName}StateManager(
                        scope = viewModelScope,
                        initialState = ${featureName}UiState.UnInitialized,
                        actionProcessor = actionProcessor,
                        reducer = ${featureName}Reducer()
                    )

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
                import androidx.lifecycle.Lifecycle
                import androidx.lifecycle.compose.LocalLifecycleOwner
                import androidx.lifecycle.compose.collectAsStateWithLifecycle
                import androidx.lifecycle.repeatOnLifecycle
                
                /**
                 * ${featureName} featureのナビゲーションルートとなるComposable関数
                 */
                @Composable
                fun ${featureName}Route(
                    viewModel: ${featureName}ViewModel = hiltViewModel()
                ) {
                    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
                    
                    val lifecycle = LocalLifecycleOwner.current
                    
                    LaunchedEffect(viewModel.uiEffect, lifecycle) {
                        lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                            viewModel.uiEffect.collect { effect ->
                                // TODO: Handle effect
                            }
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
                 * ${featureName} featureのメイン画面を表示するComposable関数
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
                println("Generated: $fileName")
            } else {
                println("Skipped (already exists): $fileName")
            }
        }
    }
}
