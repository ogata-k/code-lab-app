package com.ogata_k.mobile.code_lab.core.mvi.middleware.action

import com.ogata_k.mobile.code_lab.common.ObjectFormatter
import com.ogata_k.mobile.code_lab.common.logV
import com.ogata_k.mobile.code_lab.core.mvi.Action
import com.ogata_k.mobile.code_lab.core.mvi.ActionMiddleware
import com.ogata_k.mobile.code_lab.core.mvi.UiState

/**
 * アクションの実行時間を計測してログ出力するミドルウェア
 */
class PerformanceActionMiddleware<US : UiState, A : Action> : ActionMiddleware<US, A> {
    override suspend fun handleAction(
        getUiState: () -> US,
        action: A,
        next: suspend (A) -> Unit
    ) {
        val startTime = System.currentTimeMillis()
        next(action)
        val endTime = System.currentTimeMillis()
        val duration = endTime - startTime
        logV("PerformanceMiddleware") {
            "Action ${
                ObjectFormatter.formatAsSimple(
                    action
                )
            } took ${duration}ms"
        }
    }
}