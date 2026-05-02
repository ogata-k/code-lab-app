package com.ogata_k.mobile.code_lab.feature.home

import com.ogata_k.mobile.code_lab.core.mvi.UiEffect

/**
 * Home featureのUI副作用（ワンショットのイベント）
 */
sealed interface HomeUiEffect : UiEffect {
    data object ShowInitializedSnackbar : HomeUiEffect
}