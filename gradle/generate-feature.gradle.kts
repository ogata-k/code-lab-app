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
        val testTargetDir =
            file("app/src/test/java/com/ogata_k/mobile/code_lab/feature/$packageLabel")

        if (!targetDir.exists()) {
            targetDir.mkdirs()
        }
        if (!testTargetDir.exists()) {
            testTargetDir.mkdirs()
        }

        val templates = mapOf(
            "${featureName}UiState.kt" to """
                package $packageName
                
                import com.ogata_k.mobile.code_lab.core.mvi.UiState
                
                /**
                 * ${featureName} featureのUI状態
                 */
                sealed interface ${featureName}UiState : UiState {
                    // TODO: 本来のUiStateに書き換える
                    data object UnInitialized : ${featureName}UiState
                    data object Initialized : ${featureName}UiState
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
                    // TODO: 本来のActionに書き換える
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
                sealed interface ${featureName}Mutation : Mutation {
                    // TODO: 本来のMutationに書き換える
                    data object ToInitialized : ${featureName}Mutation
                }
            """.trimIndent(),

            "${featureName}ActionProcessor.kt" to """
                package $packageName
                
                import com.ogata_k.mobile.code_lab.core.mvi.ActionProcessor
                import com.ogata_k.mobile.code_lab.core.mvi.StoreScope
                import javax.inject.Inject
                
                /**
                 * ${featureName} featureのアクションを処理し、ミューテーションを生成するクラス
                 */
                class ${featureName}ActionProcessor @Inject constructor() : ActionProcessor<${featureName}UiState, ${featureName}UiEffect, ${featureName}Intent, ${featureName}Action, ${featureName}Mutation> {
                    override suspend fun process(
                        action: ${featureName}Action,
                        scope: StoreScope<${featureName}UiState, ${featureName}UiEffect, ${featureName}Intent, ${featureName}Action, ${featureName}Mutation>
                    ) {
                        when (action) {
                            is ${featureName}Action.Initialize -> {
                                // TODO: 実際の初期化処理
                                scope.emitMutation(${featureName}Mutation.ToInitialized)
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
                        return when(mutation){
                            ${featureName}Mutation.ToInitialized -> ${featureName}UiState.Initialized
                        }
                    }
                }
            """.trimIndent(),

            "${featureName}Store.kt" to """
                package $packageName
                
                import com.ogata_k.mobile.code_lab.global.GlobalUiController
                import com.ogata_k.mobile.code_lab.core.mvi.BaseStore
                import kotlinx.coroutines.CoroutineScope
                
                /**
                 * ${featureName} featureの状態管理を統括するクラス
                 */
                class ${featureName}Store(
                    scope: CoroutineScope,
                    initialState: ${featureName}UiState,
                    actionProcessor: ${featureName}ActionProcessor,
                    reducer: ${featureName}Reducer,
                    globalUiController: GlobalUiController
                ) : BaseStore<${featureName}UiState, ${featureName}UiEffect, ${featureName}Intent, ${featureName}Action, ${featureName}Mutation>(
                    scope = scope,
                    initialState = initialState,
                    actionProcessor = actionProcessor,
                    reducer = reducer,
                    globalUiController = globalUiController
                )
            """.trimIndent(),

            "${featureName}ViewModel.kt" to """
                package $packageName
                
                import androidx.lifecycle.viewModelScope
                import com.ogata_k.mobile.code_lab.global.GlobalUiController
                import com.ogata_k.mobile.code_lab.core.mvi.BaseViewModel
                import dagger.hilt.android.lifecycle.HiltViewModel
                import javax.inject.Inject
                
                /**
                 * ${featureName} featureのViewModel
                 */
                @HiltViewModel
                class ${featureName}ViewModel @Inject constructor(
                    actionProcessor: ${featureName}ActionProcessor,
                    globalUiController: GlobalUiController
                ) : BaseViewModel<${featureName}UiState, ${featureName}UiEffect, ${featureName}Intent, ${featureName}Action, ${featureName}Mutation>() {
                    override val store: ${featureName}Store = ${featureName}Store(
                        scope = viewModelScope,
                        initialState = ${featureName}UiState.UnInitialized,
                        actionProcessor = actionProcessor,
                        reducer = ${featureName}Reducer(),
                        globalUiController = globalUiController
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
                import androidx.compose.runtime.getValue
                import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
                import androidx.lifecycle.compose.collectAsStateWithLifecycle
                import com.ogata_k.mobile.code_lab.ui.widget.screen.AdaptiveRouteHost
                
                /**
                 * ${featureName} featureのナビゲーションルートとなるComposable関数
                 */
                @Composable
                fun ${featureName}Route(
                    viewModel: ${featureName}ViewModel = hiltViewModel()
                ) {
                    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
                    
                    AdaptiveRouteHost(
                        storeContainer = viewModel,
                        onHandleUiEffect = { effect, snackbarHostState, context, scope ->
                            // TODO: Handle effect
                        },
                    ) {
                        ${featureName}Screen(
                            uiState = uiState.featureUiState,
                            onIntent = { viewModel.dispatchIntent(it) }
                        )
                    }
                }
            """.trimIndent(),

            "${featureName}Screen.kt" to """
                package $packageName
                
                import androidx.compose.foundation.layout.Box
                import androidx.compose.foundation.layout.fillMaxSize
                import androidx.compose.foundation.layout.padding
                import androidx.compose.material3.MaterialTheme
                import androidx.compose.material3.Scaffold
                import androidx.compose.material3.Text
                import androidx.compose.material3.TopAppBar
                import androidx.compose.material3.TopAppBarDefaults
                import androidx.compose.runtime.Composable
                import androidx.compose.ui.Modifier
                import androidx.compose.ui.res.stringResource
                import androidx.compose.ui.tooling.preview.Preview
                import com.ogata_k.mobile.code_lab.R
                import com.ogata_k.mobile.code_lab.ui.theme.CodeLabTheme
                import com.ogata_k.mobile.code_lab.ui.widget.screen.ScreenContainer
                
                /**
                 * ${featureName} featureのメイン画面を表示するComposable関数
                 */
                @Composable
                fun ${featureName}Screen(
                    uiState: ${featureName}UiState,
                    onIntent: (${featureName}Intent) -> Unit,
                ) {
                    ScreenContainer {
                        Scaffold(
                            modifier = Modifier.fillMaxSize(),
                            topBar = {
                                TopAppBar(
                                    colors = TopAppBarDefaults.topAppBarColors(
                                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                                        titleContentColor = MaterialTheme.colorScheme.primary,
                                    ), title = {
                                        // @todo 必要ならタイトルを追加
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
                }
                
                @Composable
                fun Greeting(name: String, modifier: Modifier = Modifier) {
                    val appName: String = stringResource(R.string.app_name)
                    Text(
                        text = "Hello ${'$'}name for ${'$'}appName!",
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
            """.trimIndent()
        )

        templates.forEach { (fileName, content) ->
            val file = targetDir.resolve(fileName)
            if (!file.exists()) {
                file.writeText(content)
                println("Generated template: $fileName")
            } else {
                println("Skipped template (already exists): $fileName")
            }
        }


        val testTemplates = mapOf(
            "${featureName}StoreTest.kt" to """
                package $packageName
                
                import app.cash.turbine.test
                import com.ogata_k.mobile.code_lab.core.mvi.ScreenState
                import io.mockk.mockk
                import kotlinx.coroutines.ExperimentalCoroutinesApi
                import kotlinx.coroutines.test.advanceUntilIdle
                import kotlinx.coroutines.test.runTest
                import org.junit.Assert.assertEquals
                import org.junit.Test
                
                /**
                 * ${featureName}Storeのテスト
                 */
                @OptIn(ExperimentalCoroutinesApi::class)
                class ${featureName}StoreTest {
                    @Test
                    fun `初期状態がUnInitializedであること`() = runTest {
                        val actionProcessor = ${featureName}ActionProcessor()
                        val store = ${featureName}Store(
                            scope = backgroundScope,
                            initialState = ${featureName}UiState.UnInitialized,
                            actionProcessor = actionProcessor,
                            reducer = ${featureName}Reducer(),
                            globalUiController = mockk()
                        )
                
                        assertEquals(ScreenState(featureUiState = ${featureName}UiState.UnInitialized), store.uiState.value)
                    }
                
                    @Test
                    fun `Initializeアクションによって状態がInitializedに更新されること`() = runTest {
                        val actionProcessor = ${featureName}ActionProcessor()
                        val store = ${featureName}Store(
                            scope = backgroundScope,
                            initialState = ${featureName}UiState.UnInitialized,
                            actionProcessor = actionProcessor,
                            reducer = ${featureName}Reducer(),
                            globalUiController = mockk()
                        )
                
                        store.uiState.test {
                            assertEquals(ScreenState(featureUiState = ${featureName}UiState.UnInitialized), awaitItem())
                
                            store.dispatchAction(${featureName}Action.Initialize)
                
                            advanceUntilIdle()
                            assertEquals(ScreenState(featureUiState = ${featureName}UiState.Initialized), awaitItem())
                        }
                    }
                }
            """.trimIndent(),

            "${featureName}ViewModelTest.kt" to """
                package $packageName
                
                import app.cash.turbine.test
                import com.ogata_k.mobile.code_lab.core.mvi.ScreenState
                import io.mockk.mockk
                import kotlinx.coroutines.Dispatchers
                import kotlinx.coroutines.ExperimentalCoroutinesApi
                import kotlinx.coroutines.test.StandardTestDispatcher
                import kotlinx.coroutines.test.advanceUntilIdle
                import kotlinx.coroutines.test.resetMain
                import kotlinx.coroutines.test.runTest
                import kotlinx.coroutines.test.setMain
                import org.junit.After
                import org.junit.Assert.assertEquals
                import org.junit.Before
                import org.junit.Test
                
                /**
                 * ${featureName}ViewModelのテスト
                 */
                @OptIn(ExperimentalCoroutinesApi::class)
                class ${featureName}ViewModelTest {
                    private val testDispatcher = StandardTestDispatcher()

                    @Before
                    fun setup() {
                        Dispatchers.setMain(testDispatcher)
                    }

                    @After
                    fun tearDown() {
                        Dispatchers.resetMain()
                    }
                    
                    @Test
                    fun `初期化時にInitialized状態になること`() = runTest {
                        val actionProcessor = ${featureName}ActionProcessor()
                        val viewModel = ${featureName}ViewModel(actionProcessor, mockk())

                        viewModel.uiState.test {
                            // 初期状態がUnInitializedであることを確認
                            assertEquals(ScreenState(featureUiState = ${featureName}UiState.UnInitialized), awaitItem())

                            // Initializeアクションが完了するまで待機
                            advanceUntilIdle()

                            // viewModel.init内でInitializeアクションが呼ばれる想定
                            assertEquals(ScreenState(featureUiState = ${featureName}UiState.Initialized), awaitItem())
                        }
                    }
                }
            """.trimIndent(),

            "${featureName}ReducerTest.kt" to """
                package $packageName
                
                import org.junit.Assert.assertEquals
                import org.junit.Test
                
                /**
                 * ${featureName}Reducerのテスト
                 */
                class ${featureName}ReducerTest {
                    private val reducer = ${featureName}Reducer()
                
                    @Test
                    fun `ToInitializedミューテーションによりInitialized状態に遷移すること`() {
                        val initialState = ${featureName}UiState.UnInitialized
                        val mutation = ${featureName}Mutation.ToInitialized
                        
                        val newState = reducer.reduce(initialState, mutation)
                        
                        assertEquals(${featureName}UiState.Initialized, newState)
                    }
                }
            """.trimIndent()
        )

        testTemplates.forEach { (fileName, content) ->
            val file = testTargetDir.resolve(fileName)
            if (!file.exists()) {
                file.writeText(content)
                println("Generated test template: $fileName")
            } else {
                println("Skipped test template (already exists): $fileName")
            }
        }
    }
}
