package com.ogata_k.mobile.code_lab.core.mvi

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ContractTest {

    private object TestUiState : UiState
    private object TestAction : Action {
        override val strategy: ExecutionStrategy = ExecutionStrategy.Parallel
    }

    private object TestIntent : Intent<TestAction> {
        override fun toAction(): TestAction = TestAction
    }

    @Test
    fun `IntentMiddlewareのprocessがhandleIntentとnextを正しく呼び出すこと`() = runTest {
        var handleIntentCalled = false
        var nextCalled = false

        val middleware = object : IntentMiddleware<TestUiState, TestIntent, TestAction> {
            override suspend fun handleIntent(
                getUiState: () -> TestUiState,
                intent: TestIntent,
                next: suspend (TestIntent) -> Unit
            ) {
                handleIntentCalled = true
                next(intent)
            }
        }

        middleware.process({ TestUiState }, TestIntent) {
            nextCalled = true
        }

        assertTrue(handleIntentCalled)
        assertTrue(nextCalled)
    }

    @Test
    fun `ActionMiddlewareのprocessがhandleActionとnextを正しく呼び出すこと`() = runTest {
        var handleActionCalled = false
        var nextCalled = false

        val middleware = object : ActionMiddleware<TestUiState, TestAction> {
            override suspend fun handleAction(
                getUiState: () -> TestUiState,
                action: TestAction,
                next: suspend (TestAction) -> Unit
            ) {
                handleActionCalled = true
                next(action)
            }
        }

        middleware.process({ TestUiState }, TestAction) {
            nextCalled = true
        }

        assertTrue(handleActionCalled)
        assertTrue(nextCalled)
    }
}
