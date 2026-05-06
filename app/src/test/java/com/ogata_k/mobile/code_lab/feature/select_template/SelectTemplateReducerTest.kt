package com.ogata_k.mobile.code_lab.feature.select_template

import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * SelectTemplateReducerのテスト
 */
class SelectTemplateReducerTest {

    private val reducer = SelectTemplateReducer()

    @Test
    fun `ToInitializedミューテーションによりInitialized状態に遷移すること`() {
        val initialState = SelectTemplateUiState.UnInitialized
        val mutation = SelectTemplateMutation.ToInitialized

        val newState = reducer.reduce(initialState, mutation)

        assertEquals(SelectTemplateUiState.Initialized, newState)
    }
}
