package com.ogata_k.mobile.code_lab.global

import kotlinx.coroutines.flow.SharedFlow

/**
 * アプリ全体で共通のUiEffectを通知するためのコントローラー
 */
interface GlobalUiController {
    val effects: SharedFlow<GlobalUiEffect>
    suspend fun sendUiEffect(effect: GlobalUiEffect)
}