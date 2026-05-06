package com.ogata_k.mobile.code_lab.feature.select_template

import com.ogata_k.mobile.code_lab.core.mvi.Intent

/**
 * SelectTemplate featureに対するユーザーの意図（操作）
 */
sealed interface SelectTemplateIntent : Intent<SelectTemplateAction> {
    override fun toAction(): SelectTemplateAction? = when (this) {
        // TODO: Intentが増えたらここに追加
        else -> null
    }
}