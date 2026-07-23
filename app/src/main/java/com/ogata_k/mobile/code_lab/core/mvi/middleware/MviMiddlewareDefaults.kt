package com.ogata_k.mobile.code_lab.core.mvi.middleware

import com.ogata_k.mobile.code_lab.core.mvi.Action
import com.ogata_k.mobile.code_lab.core.mvi.ActionMiddleware
import com.ogata_k.mobile.code_lab.core.mvi.Intent
import com.ogata_k.mobile.code_lab.core.mvi.IntentMiddleware
import com.ogata_k.mobile.code_lab.core.mvi.UiState
import com.ogata_k.mobile.code_lab.core.mvi.middleware.action.LoggingActionMiddleware
import com.ogata_k.mobile.code_lab.core.mvi.middleware.action.PerformanceActionMiddleware
import com.ogata_k.mobile.code_lab.core.mvi.middleware.intent.LoggingIntentMiddleware
import com.ogata_k.mobile.code_lab.core.mvi.middleware.intent.ThrottleIntentMiddleware

/**
 * デフォルトで適用するミドルウェアのリストを管理するオブジェクト
 */
object MviMiddlewareDefaults {
    fun <US : UiState, I : Intent<A>, A : Action> defaultIntentMiddlewares(): List<IntentMiddleware<US, I, A>> {
        return listOf(
            LoggingIntentMiddleware(),
            ThrottleIntentMiddleware()
        )
    }

    fun <US : UiState, A : Action> defaultActionMiddlewares(): List<ActionMiddleware<US, A>> {
        return listOf(
            LoggingActionMiddleware(),
            PerformanceActionMiddleware()
        )
    }
}