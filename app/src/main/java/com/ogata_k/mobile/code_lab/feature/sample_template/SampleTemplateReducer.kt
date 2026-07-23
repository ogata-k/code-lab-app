package com.ogata_k.mobile.code_lab.feature.sample_template

import com.ogata_k.mobile.code_lab.core.mvi.Reducer

/**
 * SampleTemplate featureの現在の状態とミューテーションから新しい状態を生成するクラス
 */
class SampleTemplateReducer : Reducer<SampleTemplateUiState, SampleTemplateMutation> {
    override fun reduce(
        currentState: SampleTemplateUiState,
        mutation: SampleTemplateMutation
    ): SampleTemplateUiState {
        // TODO: 実際の変換処理
        return when (mutation) {
            SampleTemplateMutation.ToInitialized -> SampleTemplateUiState.Initialized
        }
    }
}