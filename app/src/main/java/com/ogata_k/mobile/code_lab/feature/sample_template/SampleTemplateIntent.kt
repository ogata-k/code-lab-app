package com.ogata_k.mobile.code_lab.feature.sample_template

import com.ogata_k.mobile.code_lab.core.mvi.Intent
import com.ogata_k.mobile.code_lab.core.mvi.metadata.intent.ThrottleIntent
import com.ogata_k.mobile.code_lab.core.mvi.metadata.intent.ThrottleKind
import com.ogata_k.mobile.code_lab.ui.enum.SampleFeatureDiv
import com.ogata_k.mobile.code_lab.ui.widget.dialog.CommonDialogData

/**
 * SampleTemplate featureに対するユーザーの意図（操作）
 */
sealed interface SampleTemplateIntent : Intent<SampleTemplateAction> {

    data class DismissDialog(val data: CommonDialogData) : SampleTemplateIntent

    data class TapListItem(val sampleFeatureDiv: SampleFeatureDiv) : SampleTemplateIntent,
        ThrottleIntent {
        override val throttleKind: ThrottleKind
            get() = ThrottleKind.Navigation
    }

    override fun toAction(): SampleTemplateAction? = when (this) {
        is DismissDialog -> SampleTemplateAction.DismissDialog(this.data)
        is TapListItem -> SampleTemplateAction.NavigateToSampleFeature(this.sampleFeatureDiv)
    }
}