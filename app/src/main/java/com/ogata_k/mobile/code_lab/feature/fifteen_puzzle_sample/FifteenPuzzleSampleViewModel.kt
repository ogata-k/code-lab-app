package com.ogata_k.mobile.code_lab.feature.fifteen_puzzle_sample

import androidx.lifecycle.viewModelScope
import com.ogata_k.mobile.code_lab.core.mvi.BaseViewModel
import com.ogata_k.mobile.code_lab.domain.enum.FifteenPuzzleDifficulty
import com.ogata_k.mobile.code_lab.global.GlobalUiController
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * FifteenPuzzleSample featureのViewModel
 */
@HiltViewModel
class FifteenPuzzleSampleViewModel @Inject constructor(
    actionProcessor: FifteenPuzzleSampleActionProcessor,
    globalUiController: GlobalUiController
) : BaseViewModel<FifteenPuzzleSampleUiState, FifteenPuzzleSampleUiEffect, FifteenPuzzleSampleIntent, FifteenPuzzleSampleAction, FifteenPuzzleSampleMutation>() {
    override val store: FifteenPuzzleSampleStore = FifteenPuzzleSampleStore(
        scope = viewModelScope,
        initialState = FifteenPuzzleSampleUiState.NotStart(4u, FifteenPuzzleDifficulty.Normal),
        actionProcessor = actionProcessor,
        reducer = FifteenPuzzleSampleReducer(),
        globalUiController = globalUiController
    )

    // 初回で処理する必要があるものはない
}