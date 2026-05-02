package com.ogata_k.mobile.code_lab.feature.home

import app.cash.turbine.test
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

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
    fun `初期化後にディレイを経てInitialized状態に遷移すること`() = runTest {
        val actionProcessor = HomeActionProcessor()
        // viewModel uses viewModelScope, which uses Dispatchers.Main (set to testDispatcher above)
        val viewModel = HomeViewModel(actionProcessor, mockk())

        viewModel.uiState.test {
            // Initial state from StateManager
            assertEquals(HomeUiState.UnInitialized, awaitItem())

            // init block calls Initialize action, which has 1s delay
            advanceTimeBy(1001)

            assertEquals(HomeUiState.Initialized, awaitItem())
        }
    }

    @Test
    fun `初期化時にShowInitializedSnackbarエフェクトが発行されること`() = runTest {
        val actionProcessor = HomeActionProcessor()
        val viewModel = HomeViewModel(actionProcessor, mockk())

        viewModel.uiEffect.test {
            advanceTimeBy(1001)
            assertEquals(HomeUiEffect.ShowInitializedSnackbar, awaitItem())
        }
    }
}
