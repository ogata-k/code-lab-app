package com.ogata_k.mobile.code_lab.core.mvi

import app.cash.turbine.test
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

private typealias TestScope = StateManagerScope<BaseStateManagerTest.TestUiState, BaseStateManagerTest.TestUiEffect, BaseStateManagerTest.TestMutation>

@OptIn(ExperimentalCoroutinesApi::class)
class BaseStateManagerTest {

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

    class TestStateManager(
        scope: CoroutineScope,
        initialState: TestUiState,
        actionProcessor: ActionProcessor<TestUiState, TestUiEffect, TestAction, TestMutation>,
        reducer: Reducer<TestUiState, TestMutation>,
        additionalIntentMiddlewares: List<IntentMiddleware<TestUiState, TestIntent, TestAction>> = emptyList(),
        additionalActionMiddlewares: List<ActionMiddleware<TestUiState, TestAction>> = emptyList(),
    ) : BaseStateManager<TestUiState, TestUiEffect, TestIntent, TestAction, TestMutation>(
        scope = scope,
        initialState = initialState,
        actionProcessor = actionProcessor,
        reducer = reducer,
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

        val stateManager = TestStateManager(
            scope = backgroundScope,
            initialState = TestUiState.Initial,
            actionProcessor = actionProcessor,
            reducer = reducer
        )

        stateManager.uiState.test {
            assertEquals(TestUiState.Initial, awaitItem())
            stateManager.dispatchIntent(TestIntent.Intent1("test"))
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

        val stateManager = TestStateManager(
            scope = backgroundScope,
            initialState = TestUiState.Initial,
            actionProcessor = actionProcessor,
            reducer = reducer
        )

        stateManager.uiEffect.test {
            stateManager.dispatchAction(TestAction.Action1("test"))
            assertEquals(TestUiEffect.Effect1("effect"), awaitItem())
        }
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
        val stateManager = TestStateManager(
            scope = backgroundScope,
            initialState = TestUiState.Initial,
            actionProcessor = actionProcessor,
            reducer = reducer,
            additionalIntentMiddlewares = listOf(middleware)
        )

        stateManager.dispatchIntent(TestIntent.Intent1("1"))
        stateManager.dispatchIntent(TestIntent.Intent1("2"))

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
        val stateManager = TestStateManager(
            scope = backgroundScope,
            initialState = TestUiState.Initial,
            actionProcessor = actionProcessor,
            reducer = reducer,
            additionalIntentMiddlewares = listOf(createMiddleware("M1"), createMiddleware("M2"))
        )

        stateManager.dispatchIntent(TestIntent.Intent1("test"))
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
                    scope: StateManagerScope<TestUiState, TestUiEffect, TestMutation>
                ) {
                    logs.add("process:${(action as TestAction.Action1).value}")
                }
            }

        val stateManager = TestStateManager(
            scope = backgroundScope,
            initialState = TestUiState.Initial,
            actionProcessor = actionProcessor,
            reducer = reducer
        )

        // Parallel戦略でも、dispatchされた順序で内部キューから取り出され処理が開始されることを確認
        stateManager.dispatchAction(TestAction.Action1("1"))
        stateManager.dispatchAction(TestAction.Action1("2"))
        stateManager.dispatchAction(TestAction.Action1("3"))

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
        val stateManager = TestStateManager(
            scope = backgroundScope,
            initialState = TestUiState.Initial,
            actionProcessor = actionProcessor,
            reducer = reducer,
            additionalActionMiddlewares = listOf(createMiddleware("M1"), createMiddleware("M2"))
        )

        stateManager.dispatchAction(TestAction.Action1("test"))
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

        val stateManager = TestStateManager(
            scope = backgroundScope,
            initialState = TestUiState.Initial,
            actionProcessor = actionProcessor,
            reducer = reducer
        )

        val strategy = ExecutionStrategy.Sequential(DefaultExecutionKey)
        stateManager.dispatchAction(TestAction.Action1("1", strategy))
        stateManager.dispatchAction(TestAction.Action1("2", strategy))

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

        val stateManager = TestStateManager(
            scope = backgroundScope,
            initialState = TestUiState.Initial,
            actionProcessor = actionProcessor,
            reducer = reducer
        )

        val strategy = ExecutionStrategy.LatestOnly(DefaultExecutionKey)
        stateManager.dispatchAction(TestAction.Action1("2", strategy))
        advanceTimeBy(50)
        stateManager.dispatchAction(TestAction.Action1("1", strategy))

        advanceTimeBy(150)

        assertEquals(listOf("1"), processedValues)
    }
}
