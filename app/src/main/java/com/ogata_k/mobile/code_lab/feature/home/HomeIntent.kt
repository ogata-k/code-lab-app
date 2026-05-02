package com.ogata_k.mobile.code_lab.feature.home

import com.ogata_k.mobile.code_lab.core.mvi.Intent

/**
 * Home featureに対するユーザーの意図（操作）
 */
sealed interface HomeIntent : Intent<HomeAction> {
    override fun toAction(): HomeAction? = when (this) {
        // TODO: Intentが増えたらここに追加
        else -> null
    }
}