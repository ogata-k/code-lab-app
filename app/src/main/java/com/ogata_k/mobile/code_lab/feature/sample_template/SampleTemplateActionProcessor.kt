package com.ogata_k.mobile.code_lab.feature.sample_template

import com.ogata_k.mobile.code_lab.core.mvi.ActionProcessor
import com.ogata_k.mobile.code_lab.core.mvi.StoreScope
import javax.inject.Inject

/**
 * SampleTemplate featureのアクションを処理し、ミューテーションを生成するクラス
 */
class SampleTemplateActionProcessor @Inject constructor() :
    ActionProcessor<SampleTemplateUiState, SampleTemplateUiEffect, SampleTemplateIntent, SampleTemplateAction, SampleTemplateMutation> {
    override suspend fun process(
        action: SampleTemplateAction,
        scope: StoreScope<SampleTemplateUiState, SampleTemplateUiEffect, SampleTemplateIntent, SampleTemplateAction, SampleTemplateMutation>
    ) {
        when (action) {
            is SampleTemplateAction.NavigateToSampleFeature -> {
                scope.emitUiEffect(SampleTemplateUiEffect.NavigateToSampleFeature(action.sampleFeatureDiv))
            }
        }
    }
}