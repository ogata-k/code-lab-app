package com.ogata_k.mobile.code_lab.core.mvi.middleware.intent

import com.ogata_k.mobile.code_lab.common.ObjectFormatter
import com.ogata_k.mobile.code_lab.common.logE
import com.ogata_k.mobile.code_lab.common.logI
import com.ogata_k.mobile.code_lab.core.mvi.Action
import com.ogata_k.mobile.code_lab.core.mvi.Intent
import com.ogata_k.mobile.code_lab.core.mvi.IntentMiddleware
import com.ogata_k.mobile.code_lab.core.mvi.UiState
import com.ogata_k.mobile.code_lab.core.mvi.metadata.intent.SkippableIntent

/**
 * [SkippableIntent] を実装した Intent の
 * [SkippableIntent.needSkip] が true を返す場合に処理をスキップする middleware。
 */
class SkippableIntentMiddleware<
        US : UiState,
        I : Intent<A>,
        A : Action,
        > : IntentMiddleware<US, I, A> {
    override suspend fun handleIntent(
        getUiState: () -> US,
        intent: I,
        next: suspend (I) -> Unit,
    ) {
        try {
            @Suppress("UNCHECKED_CAST")
            val skippableIntent = intent as? SkippableIntent<US>
            if (skippableIntent?.needSkip(getUiState()) == true) {
                logI("IntentMiddleware") {
                    "Skipped by SkippableIntent: ${
                        ObjectFormatter.formatAsSimple(
                            intent
                        )
                    }"
                }
                return
            }
        } catch (e: ClassCastException) {
            // 型が合わない場合はスキップせずに次に流す、などの安全策
            logE("IntentMiddleware", e) {
                "UnSupport UiState class instance: ${
                    ObjectFormatter.formatAsSimple(
                        intent
                    )
                }"
            }
        }

        next(intent)
    }
}
