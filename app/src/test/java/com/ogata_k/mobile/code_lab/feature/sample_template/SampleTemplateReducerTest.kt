package com.ogata_k.mobile.code_lab.feature.sample_template

import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * SampleTemplateReducerのテスト
 */
class SampleTemplateReducerTest {
    private val reducer = SampleTemplateReducer()

    @Test
    fun `ToInitializedミューテーションによりInitialized状態に遷移すること`() {
        val initialState = SampleTemplateUiState.UnInitialized
        val mutation = SampleTemplateMutation.ToInitialized

        val newState = reducer.reduce(initialState, mutation)

        assertEquals(SampleTemplateUiState.Initialized, newState)
    }
}