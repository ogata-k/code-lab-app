package com.ogata_k.mobile.code_lab.core.mvi.middleware.intent

import com.ogata_k.mobile.code_lab.common.logV
import com.ogata_k.mobile.code_lab.core.mvi.Action
import com.ogata_k.mobile.code_lab.core.mvi.Intent
import com.ogata_k.mobile.code_lab.core.mvi.IntentMiddleware
import com.ogata_k.mobile.code_lab.core.mvi.UiState

/**
 * すべてのIntentをログ出力するミドルウェア
 */
class LoggingIntentMiddleware<US : UiState, I : Intent<A>, A : Action> :
    IntentMiddleware<US, I, A> {
    override suspend fun handleIntent(
        getUiState: () -> US,
        intent: I,
        next: suspend (I) -> Unit
    ) {
        logV("IntentMiddleware") { "Dispatching Intent: $intent" }
        next(intent)
    }
}