package com.ogata_k.mobile.code_lab.feature.select_template

import com.ogata_k.mobile.code_lab.core.mvi.Intent
import com.ogata_k.mobile.code_lab.domain.enum.TemplateDiv

/**
 * SelectTemplate featureに対するユーザーの意図（操作）
 */
sealed interface SelectTemplateIntent : Intent<SelectTemplateAction> {
    data class NavigateToTemplate(val templateDiv: TemplateDiv) : SelectTemplateIntent

    override fun toAction(): SelectTemplateAction? = when (this) {
        is NavigateToTemplate -> SelectTemplateAction.NavigateToTemplate(this.templateDiv)
    }
}