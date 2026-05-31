package com.ogata_k.mobile.code_lab.feature.counter_sample

import com.ogata_k.mobile.code_lab.core.mvi.Intent
import com.ogata_k.mobile.code_lab.feature.counter_sample.enum.SlideOffsetDivisorType
import com.ogata_k.mobile.code_lab.ui.widget.dialog.CommonDialogData

/**
 * CounterSample featureに対するユーザーの意図（操作）
 */
sealed interface CounterSampleIntent : Intent<CounterSampleAction> {
    data class DismissDialog(val data: CommonDialogData) : CounterSampleIntent

    data object IncrementCount : CounterSampleIntent
    data object DecrementCount : CounterSampleIntent

    data class UpdateSlideDuration(val slideDurationMs: UInt) : CounterSampleIntent

    data class UpdateFadeDuration(val fadeDurationMs: UInt) : CounterSampleIntent

    data class UpdateSlideOffsetDivisor(val offsetDivisorType: SlideOffsetDivisorType) :
        CounterSampleIntent

    override fun toAction(): CounterSampleAction? = when (this) {
        is DismissDialog -> CounterSampleAction.DismissDialog(this.data)
        DecrementCount -> CounterSampleAction.DecrementCount(1.toUInt())
        IncrementCount -> CounterSampleAction.IncrementCount(1.toUInt())
        is UpdateSlideDuration -> CounterSampleAction.UpdateSlideDuration(this.slideDurationMs)
        is UpdateFadeDuration -> CounterSampleAction.UpdateFadeDuration(this.fadeDurationMs)
        is UpdateSlideOffsetDivisor -> CounterSampleAction.UpdateSlideOffsetDivisor(this.offsetDivisorType)
    }
}