package com.ogata_k.mobile.code_lab.feature.sample_template

import com.ogata_k.mobile.code_lab.core.mvi.ScreenState
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * SampleTemplateStoreのテスト
 */
@OptIn(ExperimentalCoroutinesApi::class)
class SampleTemplateStoreTest {
    @Test
    fun `初期状態がInitializedであること`() = runTest {
        val actionProcessor = SampleTemplateActionProcessor()
        val store = SampleTemplateStore(
            scope = backgroundScope,
            initialState = SampleTemplateUiState.Initialized,
            actionProcessor = actionProcessor,
            reducer = SampleTemplateReducer(),
            globalUiController = mockk()
        )

        assertEquals(
            ScreenState(featureUiState = SampleTemplateUiState.Initialized),
            store.uiState.value
        )
    }
}
