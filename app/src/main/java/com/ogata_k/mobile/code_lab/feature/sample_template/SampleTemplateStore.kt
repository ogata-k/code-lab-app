package com.ogata_k.mobile.code_lab.feature.sample_template

import com.ogata_k.mobile.code_lab.core.mvi.BaseStore
import com.ogata_k.mobile.code_lab.global.GlobalUiController
import kotlinx.coroutines.CoroutineScope

/**
 * SampleTemplate featureの状態管理を統括するクラス
 */
class SampleTemplateStore(
    scope: CoroutineScope,
    initialState: SampleTemplateUiState,
    actionProcessor: SampleTemplateActionProcessor,
    reducer: SampleTemplateReducer,
    globalUiController: GlobalUiController
) : BaseStore<SampleTemplateUiState, SampleTemplateUiEffect, SampleTemplateIntent, SampleTemplateAction, SampleTemplateMutation>(
    scope = scope,
    initialState = initialState,
    actionProcessor = actionProcessor,
    reducer = reducer,
    globalUiController = globalUiController
)