package com.ogata_k.mobile.code_lab.feature.sample_template

import androidx.lifecycle.viewModelScope
import com.ogata_k.mobile.code_lab.core.mvi.BaseViewModel
import com.ogata_k.mobile.code_lab.global.GlobalUiController
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * SampleTemplate featureのViewModel
 */
@HiltViewModel
class SampleTemplateViewModel @Inject constructor(
    actionProcessor: SampleTemplateActionProcessor,
    globalUiController: GlobalUiController
) : BaseViewModel<SampleTemplateUiState, SampleTemplateUiEffect, SampleTemplateIntent, SampleTemplateAction, SampleTemplateMutation>() {
    override val store: SampleTemplateStore = SampleTemplateStore(
        scope = viewModelScope,
        initialState = SampleTemplateUiState.UnInitialized,
        actionProcessor = actionProcessor,
        reducer = SampleTemplateReducer(),
        globalUiController = globalUiController
    )

    init {
        // 初期データのロード
        dispatchAction(SampleTemplateAction.Initialize)
    }
}