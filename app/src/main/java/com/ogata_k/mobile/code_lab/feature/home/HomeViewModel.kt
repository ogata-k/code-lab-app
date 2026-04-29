package com.ogata_k.mobile.code_lab.feature.home

import com.ogata_k.mobile.code_lab.feature.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * Home の ViewModel
 */
@HiltViewModel
class HomeViewModel @Inject constructor(
    processor: HomeActionProcessor
) : BaseViewModel<HomeUiState, HomeUiEffect, HomeIntent, HomeMutation>(
    initialState = TODO("NEXT set initial state here FOR HomeUiState"),
    stateManager = HomeStateManager(processor = processor),
    reducer = HomeReducer()
)