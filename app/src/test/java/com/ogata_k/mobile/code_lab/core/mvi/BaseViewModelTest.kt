package com.ogata_k.mobile.code_lab.core.mvi

import app.cash.turbine.test
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class BaseViewModelTest {

    // Test implementations
    sealed interface TestUiState : UiState {
        object Initial : TestUiState
    }

    sealed interface TestUiEffect : UiEffect
    sealed interface TestAction : Action
    interface TestIntent : Intent<TestAction>
    sealed interface TestMutation : Mutation

    class TestViewModel(
        override val stateManager: BaseStateManager<TestUiState, TestUiEffect, TestIntent, TestAction, TestMutation>
    ) : BaseViewModel<TestUiState, TestUiEffect, TestIntent, TestAction, TestMutation>()

    @Test
    fun `uiStateがstateManagerに委譲されていること`() = runTest {
        val stateFlow = MutableStateFlow<TestUiState>(TestUiState.Initial)
        val stateManager =
            mockk<BaseStateManager<TestUiState, TestUiEffect, TestIntent, TestAction, TestMutation>>()
        coEvery { stateManager.uiState } returns stateFlow.asStateFlow()

        val viewModel = TestViewModel(stateManager)

        viewModel.uiState.test {
            assertEquals(TestUiState.Initial, awaitItem())
        }
    }

    @Test
    fun `dispatchIntentがstateManagerに委譲されていること`() = runTest {
        val stateManager =
            mockk<BaseStateManager<TestUiState, TestUiEffect, TestIntent, TestAction, TestMutation>>(
                relaxed = true
            )
        val viewModel = TestViewModel(stateManager)
        val intent = mockk<TestIntent>()

        viewModel.dispatchIntent(intent)

        io.mockk.verify { stateManager.dispatchIntent(intent) }
    }
}
