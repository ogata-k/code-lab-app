package com.ogata_k.mobile.code_lab.feature.counter_sample

import com.ogata_k.mobile.code_lab.core.mvi.ActionProcessor
import com.ogata_k.mobile.code_lab.core.mvi.CommonUiEffect
import com.ogata_k.mobile.code_lab.core.mvi.StoreScope
import com.ogata_k.mobile.code_lab.feature.counter_sample.CounterSampleMutation.AddCount
import com.ogata_k.mobile.code_lab.feature.counter_sample.CounterSampleMutation.SetFadeDurationMs
import com.ogata_k.mobile.code_lab.feature.counter_sample.CounterSampleMutation.SetSlideDurationMs
import com.ogata_k.mobile.code_lab.feature.counter_sample.CounterSampleMutation.SetSlideOffsetDivisor
import com.ogata_k.mobile.code_lab.ui.widget.snackbar.CommonSnackbarData
import com.ogata_k.mobile.code_lab.ui.widget.snackbar.CommonSnackbarMessage
import javax.inject.Inject

/**
 * CounterSample featureのアクションを処理し、ミューテーションを生成するクラス
 */
class CounterSampleActionProcessor @Inject constructor() :
    ActionProcessor<CounterSampleUiState, CounterSampleUiEffect, CounterSampleIntent, CounterSampleAction, CounterSampleMutation> {
    override suspend fun process(
        action: CounterSampleAction,
        scope: StoreScope<CounterSampleUiState, CounterSampleUiEffect, CounterSampleIntent, CounterSampleAction, CounterSampleMutation>
    ) {
        when (action) {
            is CounterSampleAction.DismissDialog -> {
                scope.removeDialog(action.data)
            }

            is CounterSampleAction.DecrementCount -> {
                scope.emitMutation(AddCount(-action.amount.toInt()))
            }

            is CounterSampleAction.IncrementCount -> {
                scope.emitMutation(AddCount(+action.amount.toInt()))
            }

            is CounterSampleAction.UpdateSlideDuration -> {
                val durationMs = action.slideDurationMs

                // try 120 (snappy) / 800 (luxurious)
                if (durationMs !in 120u..800u) {
                    scope.emitCommonUiEffect(
                        CommonUiEffect.ShowSnackbar(
                            data = CommonSnackbarData(
                                message = CommonSnackbarMessage.ValueOutOfRange
                            )
                        )
                    )

                    // アラートは出すが、近い値で近似して渡しておく
                    scope.emitMutation(SetSlideDurationMs(durationMs.coerceIn(120u, 800u)))
                    return
                }

                scope.emitMutation(SetSlideDurationMs(durationMs))
            }

            is CounterSampleAction.UpdateFadeDuration -> {
                val durationMs = action.fadeDurationMs

                // try 0 (no fade) / 600 (long crossfade)
                if (durationMs !in 0u..600u) {
                    scope.emitCommonUiEffect(
                        CommonUiEffect.ShowSnackbar(
                            data = CommonSnackbarData(
                                message = CommonSnackbarMessage.ValueOutOfRange
                            )
                        )
                    )

                    // アラートは出すが、近い値で近似して渡しておく
                    scope.emitMutation(SetFadeDurationMs(durationMs.coerceIn(0u, 600u)))
                    return
                }

                scope.emitMutation(SetFadeDurationMs(durationMs))
            }

            is CounterSampleAction.UpdateSlideOffsetDivisor -> {
                scope.emitMutation(SetSlideOffsetDivisor(action.offsetDivisorType))
            }
        }
    }
}