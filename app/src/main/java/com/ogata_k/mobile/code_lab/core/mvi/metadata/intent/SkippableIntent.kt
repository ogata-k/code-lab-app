package com.ogata_k.mobile.code_lab.core.mvi.metadata.intent

import com.ogata_k.mobile.code_lab.core.mvi.UiState

/**
 * わたってくる現在のUI状態をもとにスキップできるかどうかを判定するIntentのマーカーインターフェース
 */
interface SkippableIntent<US : UiState> {
    /**
     * 直前に実行したかどうかを気にせずに現状のUI状態を見てスキップする必要があるならtrueを返す
     */
    fun needSkip(
        uiState: US,
    ): Boolean
}