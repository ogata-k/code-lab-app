package com.ogata_k.mobile.code_lab.feature.select_template

import androidx.lifecycle.viewModelScope
import com.ogata_k.mobile.code_lab.core.mvi.BaseViewModel
import com.ogata_k.mobile.code_lab.global.GlobalUiController
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * SelectTemplate featureのViewModel
 */
@HiltViewModel
class SelectTemplateViewModel @Inject constructor(
    actionProcessor: SelectTemplateActionProcessor,
    globalUiController: GlobalUiController,
) : BaseViewModel<SelectTemplateUiState, SelectTemplateUiEffect, SelectTemplateIntent, SelectTemplateAction, SelectTemplateMutation>() {
    override val store: SelectTemplateStore = SelectTemplateStore(
        scope = viewModelScope,
        initialState = SelectTemplateUiState.UnInitialized,
        actionProcessor = actionProcessor,
        reducer = SelectTemplateReducer(),
        globalUiController = globalUiController
    )

    init {
        // 初期データのロード
        dispatchAction(SelectTemplateAction.Initialize)
    }
}