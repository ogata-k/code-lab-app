package com.ogata_k.mobile.code_lab.feature.sample_template

import com.ogata_k.mobile.code_lab.core.mvi.Intent
import com.ogata_k.mobile.code_lab.core.mvi.metadata.intent.ThrottleIntent
import com.ogata_k.mobile.code_lab.core.mvi.metadata.intent.ThrottleKind

/**
 * SampleTemplate featureに対するユーザーの意図（操作）
 */
sealed interface SampleTemplateIntent : Intent<SampleTemplateAction> {

    data object TapListItem : SampleTemplateIntent, ThrottleIntent {
        override val kind: ThrottleKind
            get() = ThrottleKind.Navigation
    }

    override fun toAction(): SampleTemplateAction? = when (this) {
        TapListItem -> SampleTemplateAction.NavigateToCounter
    }
}