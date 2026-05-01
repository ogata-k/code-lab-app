package com.ogata_k.mobile.code_lab.core.mvi.middleware.action

import com.ogata_k.mobile.code_lab.common.logV
import com.ogata_k.mobile.code_lab.core.mvi.Action
import com.ogata_k.mobile.code_lab.core.mvi.ActionMiddleware
import com.ogata_k.mobile.code_lab.core.mvi.UiState

/**
 * すべてのアクションをログ出力するミドルウェア
 */
class LoggingActionMiddleware<US : UiState, A : Action> : ActionMiddleware<US, A> {
    override suspend fun handleAction(
        getUiState: () -> US,
        action: A,
        next: suspend (A) -> Unit
    ) {
        logV("ActionMiddleware") { "Executing Action: $action" }
        next(action)
        logV("ActionMiddleware") { "Executed Action: $action" }
    }
}