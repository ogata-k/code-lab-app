package com.ogata_k.mobile.code_lab.feature.fifteen_puzzle_sample

import com.ogata_k.mobile.code_lab.core.mvi.UiEffect
import kotlin.time.Duration

/**
 * FifteenPuzzleSample featureのUI副作用（ワンショットのイベント）
 */
sealed interface FifteenPuzzleSampleUiEffect : UiEffect {
    data object ShowDifficultyMismatchError : FifteenPuzzleSampleUiEffect

    data class PlayClearAnimation(val navigateToClearStateDuration: Duration) :
        FifteenPuzzleSampleUiEffect
}