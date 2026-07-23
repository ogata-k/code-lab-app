package com.ogata_k.mobile.code_lab.feature.select_template

import com.ogata_k.mobile.code_lab.core.mvi.Reducer

/**
 * SelectTemplate featureの現在の状態とミューテーションから新しい状態を生成するクラス
 */
class SelectTemplateReducer : Reducer<SelectTemplateUiState, SelectTemplateMutation> {
    override fun reduce(
        currentState: SelectTemplateUiState,
        mutation: SelectTemplateMutation
    ): SelectTemplateUiState {
        return when (mutation) {
            SelectTemplateMutation.ToInitialized -> SelectTemplateUiState.Initialized
        }
    }
}