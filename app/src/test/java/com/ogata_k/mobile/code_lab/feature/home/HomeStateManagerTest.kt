package com.ogata_k.mobile.code_lab.feature.home

import app.cash.turbine.test
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomeStateManagerTest {

    @Test
    fun `初期状態がUnInitializedであること`() = runTest {
        val actionProcessor = HomeActionProcessor()
        val stateManager = HomeStateManager(
            scope = backgroundScope,
            initialState = HomeUiState.UnInitialized,
            actionProcessor = actionProcessor,
            reducer = HomeReducer(),
            globalUiController = mockk()
        )

        assertEquals(HomeUiState.UnInitialized, stateManager.uiState.value)
    }

    @Test
    fun `Initializeアクションによって状態がInitializedに更新されること`() = runTest {
        val actionProcessor = HomeActionProcessor()
        val stateManager = HomeStateManager(
            scope = backgroundScope,
            initialState = HomeUiState.UnInitialized,
            actionProcessor = actionProcessor,
            reducer = HomeReducer(),
            globalUiController = mockk()
        )

        stateManager.uiState.test {
            assertEquals(HomeUiState.UnInitialized, awaitItem())

            stateManager.dispatchAction(HomeAction.Initialize)

            advanceTimeBy(1001)
            assertEquals(HomeUiState.Initialized, awaitItem())
        }
    }

    @Test
    fun `InitializeアクションによってSnackbar表示エフェクトが発行されること`() = runTest {
        val actionProcessor = HomeActionProcessor()
        val stateManager = HomeStateManager(
            scope = backgroundScope,
            initialState = HomeUiState.UnInitialized,
            actionProcessor = actionProcessor,
            reducer = HomeReducer(),
            globalUiController = mockk()
        )

        stateManager.uiEffect.test {
            stateManager.dispatchAction(HomeAction.Initialize)

            advanceTimeBy(1001)
            assertEquals(HomeUiEffect.ShowInitializedSnackbar, awaitItem())
        }
    }
}
