package com.ogata_k.mobile.code_lab.feature.counter_sample

import com.ogata_k.mobile.code_lab.core.mvi.Reducer

/**
 * CounterSample featureの現在の状態とミューテーションから新しい状態を生成するクラス
 */
class CounterSampleReducer : Reducer<CounterSampleUiState, CounterSampleMutation> {
    override fun reduce(
        currentState: CounterSampleUiState,
        mutation: CounterSampleMutation
    ): CounterSampleUiState {
        return when (mutation) {
            is CounterSampleMutation.AddCount ->
                currentState.copy(count = currentState.count + mutation.diff)

            is CounterSampleMutation.SetSlideDurationMs ->
                currentState.copy(
                    slideDurationMs = mutation.slideDurationMs
                )

            is CounterSampleMutation.SetFadeDurationMs -> currentState.copy(
                fadeDurationMs = mutation.fadeDurationMs
            )

            is CounterSampleMutation.SetSlideOffsetDivisor -> currentState.copy(
                slideOffsetDivisor = mutation.slideOffsetDivisorType
            )
        }
    }
}