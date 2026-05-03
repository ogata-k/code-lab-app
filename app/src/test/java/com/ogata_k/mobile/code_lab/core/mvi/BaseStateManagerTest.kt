package com.ogata_k.mobile.code_lab.core.mvi

import app.cash.turbine.test
import com.ogata_k.mobile.code_lab.common.global_ui.GlobalUiController
import com.ogata_k.mobile.code_lab.common.global_ui.GlobalUiEffect
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

private typealias TestScope = StoreScope<BaseStoreTest.TestUiState, BaseStoreTest.TestUiEffect, BaseStoreTest.TestMutation>

@OptIn(ExperimentalCoroutinesApi::class)
class BaseStoreTest {

    // Test implementations
    sealed interface TestUiState : UiState {
        object Initial : TestUiState
        data class Updated(val value: String) : TestUiState
    }

    sealed interface TestUiEffect : UiEffect {
        data class Effect1(val message: String) : TestUiEffect
    }

    sealed interface TestAction : Action {
        data class Action1(
            val value: String,
            override val strategy: ExecutionStrategy = ExecutionStrategy.Parallel
        ) : TestAction
    }

    sealed interface TestIntent : Intent<TestAction> {
        data class Intent1(val value: String) : TestIntent {
            override fun toAction(): TestAction = TestAction.Action1(value)
        }
    }

    sealed interface TestMutation : Mutation {
        data class Mutation1(val value: String) : TestMutation
    }

    private val reducer = object : Reducer<TestUiState, TestMutation> {
        override fun reduce(currentState: TestUiState, mutation: TestMutation): TestUiState {
            return when (mutation) {
                is TestMutation.Mutation1 -> TestUiState.Updated(mutation.value)
            }
        }
    }

    class TestStore(
        scope: CoroutineScope,
        initialState: TestUiState,
        actionProcessor: ActionProcessor<TestUiState, TestUiEffect, TestAction, TestMutation>,
        reducer: Reducer<TestUiState, TestMutation>,
        private val onGlobalEffect: (suspend (GlobalUiEffect) -> Unit)? = null,
        additionalIntentMiddlewares: List<IntentMiddleware<TestUiState, TestIntent, TestAction>> = emptyList(),
        additionalActionMiddlewares: List<ActionMiddleware<TestUiState, TestAction>> = emptyList(),
    ) : BaseStore<TestUiState, TestUiEffect, TestIntent, TestAction, TestMutation>(
        scope = scope,
        initialState = initialState,
        actionProcessor = actionProcessor,
        reducer = reducer,
        globalUiController = object : GlobalUiController {
            override val effects: SharedFlow<GlobalUiEffect> = MutableSharedFlow()

            override suspend fun sendUiEffect(effect: GlobalUiEffect) {
                onGlobalEffect?.invoke(effect)
            }
        },
        additionalIntentMiddlewares = additionalIntentMiddlewares,
        additionalActionMiddlewares = additionalActionMiddlewares,
    )

    @Test
    fun `dispatchIntentによって最終的にuiStateが更新されること`() = runTest {
        val actionProcessor =
            mockk<ActionProcessor<TestUiState, TestUiEffect, TestAction, TestMutation>>()
        coEvery { actionProcessor.process(any(), any()) } coAnswers {
            val action = firstArg<TestAction.Action1>()
            secondArg<TestScope>().emitMutation(TestMutation.Mutation1(action.value))
        }

        val store = TestStore(
            scope = backgroundScope,
            initialState = TestUiState.Initial,
            actionProcessor = actionProcessor,
            reducer = reducer
        )

        store.uiState.test {
            assertEquals(TestUiState.Initial, awaitItem())
            store.dispatchIntent(TestIntent.Intent1("test"))
            assertEquals(TestUiState.Updated("test"), awaitItem())
        }
    }

    @Test
    fun `emitUiEffectによってuiEffectフローが通知されること`() = runTest {
        val actionProcessor =
            mockk<ActionProcessor<TestUiState, TestUiEffect, TestAction, TestMutation>>()
        coEvery { actionProcessor.process(any(), any()) } coAnswers {
            secondArg<TestScope>().emitUiEffect(TestUiEffect.Effect1("effect"))
        }

        val store = TestStore(
            scope = backgroundScope,
            initialState = TestUiState.Initial,
            actionProcessor = actionProcessor,
            reducer = reducer
        )

        store.uiEffect.test {
            store.dispatchAction(TestAction.Action1("test"))
            assertEquals(TestUiEffect.Effect1("effect"), awaitItem())
        }
    }

    @Test
    fun `emitGlobalEffectによってGlobalUiControllerに通知されること`() = runTest {
        val actionProcessor =
            mockk<ActionProcessor<TestUiState, TestUiEffect, TestAction, TestMutation>>()
        val effect = mockk<GlobalUiEffect>()
        var capturedEffect: GlobalUiEffect? = null

        coEvery { actionProcessor.process(any(), any()) } coAnswers {
            secondArg<TestScope>().emitGlobalUiEffect(effect)
        }

        val store = TestStore(
            scope = backgroundScope,
            initialState = TestUiState.Initial,
            actionProcessor = actionProcessor,
            reducer = reducer,
            onGlobalEffect = { capturedEffect = it }
        )

        store.dispatchAction(TestAction.Action1("test"))
        delay(50)

        assertEquals(effect, capturedEffect)
    }

    @Test
    fun `Intentが直列にdispatchされること`() = runTest {
        val logs = mutableListOf<String>()
        val middleware = object : IntentMiddleware<TestUiState, TestIntent, TestAction> {
            override suspend fun handleIntent(
                getUiState: () -> TestUiState,
                intent: TestIntent,
                next: suspend (TestIntent) -> Unit
            ) {
                logs.add("start:${(intent as TestIntent.Intent1).value}")
                delay(100)
                logs.add("end:${intent.value}")
                next(intent)
            }
        }

        val actionProcessor =
            mockk<ActionProcessor<TestUiState, TestUiEffect, TestAction, TestMutation>>(relaxed = true)
        val store = TestStore(
            scope = backgroundScope,
            initialState = TestUiState.Initial,
            actionProcessor = actionProcessor,
            reducer = reducer,
            additionalIntentMiddlewares = listOf(middleware)
        )

        store.dispatchIntent(TestIntent.Intent1("1"))
        store.dispatchIntent(TestIntent.Intent1("2"))

        advanceTimeBy(250)

        // Intentは常に直列に処理されるため、1つ目が終わってから2つ目が始まる
        assertEquals(listOf("start:1", "end:1", "start:2", "end:2"), logs)
    }

    @Test
    fun `複数のIntentミドルウェアが設定された順序で実行されること`() = runTest {
        val logs = mutableListOf<String>()
        fun createMiddleware(name: String) =
            object : IntentMiddleware<TestUiState, TestIntent, TestAction> {
                override suspend fun handleIntent(
                    getUiState: () -> TestUiState,
                    intent: TestIntent,
                    next: suspend (TestIntent) -> Unit
                ) {
                    logs.add("$name:before")
                    next(intent)
                    logs.add("$name:after")
                }
            }

        val actionProcessor =
            mockk<ActionProcessor<TestUiState, TestUiEffect, TestAction, TestMutation>>(relaxed = true)
        val store = TestStore(
            scope = backgroundScope,
            initialState = TestUiState.Initial,
            actionProcessor = actionProcessor,
            reducer = reducer,
            additionalIntentMiddlewares = listOf(createMiddleware("M1"), createMiddleware("M2"))
        )

        store.dispatchIntent(TestIntent.Intent1("test"))
        delay(50)

        // 順序: M1:before -> M2:before -> (Action変換/処理) -> M2:after -> M1:after
        assertEquals(listOf("M1:before", "M2:before", "M2:after", "M1:after"), logs)
    }

    @Test
    fun `Actionが直列にdispatchされること`() = runTest {
        val logs = mutableListOf<String>()
        val actionProcessor =
            object : ActionProcessor<TestUiState, TestUiEffect, TestAction, TestMutation> {
                override suspend fun process(
                    action: TestAction,
                    scope: StoreScope<TestUiState, TestUiEffect, TestMutation>
                ) {
                    logs.add("process:${(action as TestAction.Action1).value}")
                }
            }

        val store = TestStore(
            scope = backgroundScope,
            initialState = TestUiState.Initial,
            actionProcessor = actionProcessor,
            reducer = reducer
        )

        // Parallel戦略でも、dispatchされた順序で内部キューから取り出され処理が開始されることを確認
        store.dispatchAction(TestAction.Action1("1"))
        store.dispatchAction(TestAction.Action1("2"))
        store.dispatchAction(TestAction.Action1("3"))

        delay(50)

        assertEquals(listOf("process:1", "process:2", "process:3"), logs)
    }

    @Test
    fun `複数のActionミドルウェアが設定された順序で実行されること`() = runTest {
        val logs = mutableListOf<String>()
        fun createMiddleware(name: String) = object : ActionMiddleware<TestUiState, TestAction> {
            override suspend fun handleAction(
                getUiState: () -> TestUiState,
                action: TestAction,
                next: suspend (TestAction) -> Unit
            ) {
                logs.add("$name:before")
                next(action)
                logs.add("$name:after")
            }
        }

        val actionProcessor =
            mockk<ActionProcessor<TestUiState, TestUiEffect, TestAction, TestMutation>>(relaxed = true)
        val store = TestStore(
            scope = backgroundScope,
            initialState = TestUiState.Initial,
            actionProcessor = actionProcessor,
            reducer = reducer,
            additionalActionMiddlewares = listOf(createMiddleware("M1"), createMiddleware("M2"))
        )

        store.dispatchAction(TestAction.Action1("test"))
        delay(50)

        // 順序: M1:before -> M2:before -> (Processor処理) -> M2:after -> M1:after
        assertEquals(listOf("M1:before", "M2:before", "M2:after", "M1:after"), logs)
    }

    @Test
    fun `Sequential戦略でActionが一つずつ順番に処理されること`() = runTest {
        val actionProcessor =
            mockk<ActionProcessor<TestUiState, TestUiEffect, TestAction, TestMutation>>()
        val processedValues = mutableListOf<String>()

        coEvery { actionProcessor.process(any(), any()) } coAnswers {
            val action = it.invocation.args[0] as TestAction.Action1
            delay(100)
            processedValues.add(action.value)
        }

        val store = TestStore(
            scope = backgroundScope,
            initialState = TestUiState.Initial,
            actionProcessor = actionProcessor,
            reducer = reducer
        )

        val strategy = ExecutionStrategy.Sequential(DefaultExecutionKey)
        store.dispatchAction(TestAction.Action1("1", strategy))
        store.dispatchAction(TestAction.Action1("2", strategy))

        advanceTimeBy(250)

        assertEquals(listOf("1", "2"), processedValues)
    }

    @Test
    fun `LatestOnly戦略で前のActionがキャンセルされ最新のみが実行されること`() = runTest {
        val actionProcessor =
            mockk<ActionProcessor<TestUiState, TestUiEffect, TestAction, TestMutation>>()
        val processedValues = mutableListOf<String>()

        coEvery { actionProcessor.process(any(), any()) } coAnswers {
            val action = it.invocation.args[0] as TestAction.Action1
            delay(action.value.toInt() * 100L)
            processedValues.add(action.value)
        }

        val store = TestStore(
            scope = backgroundScope,
            initialState = TestUiState.Initial,
            actionProcessor = actionProcessor,
            reducer = reducer
        )

        val strategy = ExecutionStrategy.LatestOnly(DefaultExecutionKey)
        store.dispatchAction(TestAction.Action1("2", strategy))
        advanceTimeBy(50)
        store.dispatchAction(TestAction.Action1("1", strategy))

        advanceTimeBy(150)

        assertEquals(listOf("1"), processedValues)
    }
}
