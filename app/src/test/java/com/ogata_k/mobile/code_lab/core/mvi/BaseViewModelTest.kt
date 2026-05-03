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
        override val store: BaseStore<TestUiState, TestUiEffect, TestIntent, TestAction, TestMutation>
    ) : BaseViewModel<TestUiState, TestUiEffect, TestIntent, TestAction, TestMutation>()

    @Test
    fun `uiStateがstoreに委譲されていること`() = runTest {
        val stateFlow = MutableStateFlow<TestUiState>(TestUiState.Initial)
        val store =
            mockk<BaseStore<TestUiState, TestUiEffect, TestIntent, TestAction, TestMutation>>()
        coEvery { store.uiState } returns stateFlow.asStateFlow()

        val viewModel = TestViewModel(store)

        viewModel.uiState.test {
            assertEquals(TestUiState.Initial, awaitItem())
        }
    }

    @Test
    fun `dispatchIntentがstoreに委譲されていること`() = runTest {
        val store =
            mockk<BaseStore<TestUiState, TestUiEffect, TestIntent, TestAction, TestMutation>>(
                relaxed = true
            )
        val viewModel = TestViewModel(store)
        val intent = mockk<TestIntent>()

        viewModel.dispatchIntent(intent)

        io.mockk.verify { store.dispatchIntent(intent) }
    }
}
