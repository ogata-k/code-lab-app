package com.ogata_k.mobile.code_lab.feature.fifteen_puzzle_sample

import com.ogata_k.mobile.code_lab.core.mvi.BaseStore
import com.ogata_k.mobile.code_lab.global.GlobalUiController
import kotlinx.coroutines.CoroutineScope

/**
 * FifteenPuzzleSample featureの状態管理を統括するクラス
 */
class FifteenPuzzleSampleStore(
    scope: CoroutineScope,
    initialState: FifteenPuzzleSampleUiState,
    actionProcessor: FifteenPuzzleSampleActionProcessor,
    reducer: FifteenPuzzleSampleReducer,
    globalUiController: GlobalUiController
) : BaseStore<FifteenPuzzleSampleUiState, FifteenPuzzleSampleUiEffect, FifteenPuzzleSampleIntent, FifteenPuzzleSampleAction, FifteenPuzzleSampleMutation>(
    scope = scope,
    initialState = initialState,
    actionProcessor = actionProcessor,
    reducer = reducer,
    globalUiController = globalUiController
)