package com.ogata_k.mobile.code_lab.feature.sample_template

import app.cash.turbine.test
import com.ogata_k.mobile.code_lab.core.mvi.ScreenState
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * SampleTemplateStoreのテスト
 */
@OptIn(ExperimentalCoroutinesApi::class)
class SampleTemplateStoreTest {
    @Test
    fun `初期状態がUnInitializedであること`() = runTest {
        val actionProcessor = SampleTemplateActionProcessor()
        val store = SampleTemplateStore(
            scope = backgroundScope,
            initialState = SampleTemplateUiState.UnInitialized,
            actionProcessor = actionProcessor,
            reducer = SampleTemplateReducer(),
            globalUiController = mockk()
        )

        assertEquals(
            ScreenState(featureUiState = SampleTemplateUiState.UnInitialized),
            store.uiState.value
        )
    }

    @Test
    fun `Initializeアクションによって状態がInitializedに更新されること`() = runTest {
        val actionProcessor = SampleTemplateActionProcessor()
        val store = SampleTemplateStore(
            scope = backgroundScope,
            initialState = SampleTemplateUiState.UnInitialized,
            actionProcessor = actionProcessor,
            reducer = SampleTemplateReducer(),
            globalUiController = mockk()
        )

        store.uiState.test {
            assertEquals(
                ScreenState(featureUiState = SampleTemplateUiState.UnInitialized),
                awaitItem()
            )

            store.dispatchAction(SampleTemplateAction.Initialize)

            advanceUntilIdle()
            assertEquals(
                ScreenState(featureUiState = SampleTemplateUiState.Initialized),
                awaitItem()
            )
        }
    }
}