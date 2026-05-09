package com.ogata_k.mobile.code_lab.feature.sample_template

import com.ogata_k.mobile.code_lab.core.mvi.Intent

/**
 * SampleTemplate featureに対するユーザーの意図（操作）
 */
sealed interface SampleTemplateIntent : Intent<SampleTemplateAction> {
    override fun toAction(): SampleTemplateAction? = when (this) {
        // TODO: Intentが増えたらここに追加
        else -> null
    }
}