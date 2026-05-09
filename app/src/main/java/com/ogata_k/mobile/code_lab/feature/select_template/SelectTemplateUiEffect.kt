package com.ogata_k.mobile.code_lab.feature.select_template

import com.ogata_k.mobile.code_lab.core.mvi.UiEffect
import com.ogata_k.mobile.code_lab.domain.enum.TemplateDiv

/**
 * SelectTemplate featureのUI副作用（ワンショットのイベント）
 */
sealed interface SelectTemplateUiEffect : UiEffect {
    data class NavigateToTemplate(val templateDiv: TemplateDiv) : SelectTemplateUiEffect
}
