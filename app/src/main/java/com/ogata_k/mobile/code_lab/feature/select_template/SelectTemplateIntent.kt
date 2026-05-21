package com.ogata_k.mobile.code_lab.feature.select_template

import com.ogata_k.mobile.code_lab.core.mvi.Intent
import com.ogata_k.mobile.code_lab.core.mvi.metadata.intent.ThrottleIntent
import com.ogata_k.mobile.code_lab.core.mvi.metadata.intent.ThrottleKind
import com.ogata_k.mobile.code_lab.domain.enum.TemplateDiv

/**
 * SelectTemplate featureに対するユーザーの意図（操作）
 */
sealed interface SelectTemplateIntent : Intent<SelectTemplateAction> {
    data class TapListItem(val templateDiv: TemplateDiv) : SelectTemplateIntent, ThrottleIntent {
        override val throttleKind: ThrottleKind
            get() = ThrottleKind.Navigation
    }

    override fun toAction(): SelectTemplateAction? = when (this) {
        is TapListItem -> SelectTemplateAction.NavigateToTemplate(this.templateDiv)
    }
}