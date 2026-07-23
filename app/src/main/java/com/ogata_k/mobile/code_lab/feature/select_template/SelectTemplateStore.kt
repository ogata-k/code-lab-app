package com.ogata_k.mobile.code_lab.feature.select_template

import com.ogata_k.mobile.code_lab.core.mvi.BaseStore
import com.ogata_k.mobile.code_lab.global.GlobalUiController
import kotlinx.coroutines.CoroutineScope

/**
 * SelectTemplate featureの状態管理を統括するクラス
 */
class SelectTemplateStore(
    scope: CoroutineScope,
    initialState: SelectTemplateUiState,
    actionProcessor: SelectTemplateActionProcessor,
    reducer: SelectTemplateReducer,
    globalUiController: GlobalUiController
) : BaseStore<SelectTemplateUiState, SelectTemplateUiEffect, SelectTemplateIntent, SelectTemplateAction, SelectTemplateMutation>(
    scope = scope,
    initialState = initialState,
    actionProcessor = actionProcessor,
    reducer = reducer,
    globalUiController = globalUiController
)