package com.ogata_k.mobile.code_lab.core.mvi.middleware.intent

import android.os.SystemClock
import com.ogata_k.mobile.code_lab.common.ObjectFormatter
import com.ogata_k.mobile.code_lab.common.logI
import com.ogata_k.mobile.code_lab.core.mvi.Action
import com.ogata_k.mobile.code_lab.core.mvi.Intent
import com.ogata_k.mobile.code_lab.core.mvi.IntentMiddleware
import com.ogata_k.mobile.code_lab.core.mvi.UiState
import com.ogata_k.mobile.code_lab.core.mvi.metadata.intent.ThrottleIntent
import com.ogata_k.mobile.code_lab.core.mvi.metadata.intent.ThrottleKind
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.concurrent.ConcurrentHashMap

/**
 * ThrottleIntent を実装した Intent の
 * 短時間連続実行を抑制する middleware。
 *
 * ---
 *
 * throttle の判定単位:
 *
 * - Navigation:
 *      アプリ全体で共有される throttle。
 *      ListDetailPane など複数 Store からの
 *      同時 navigation を防止する。
 *
 * - ButtonSpamPrevention:
 *      middleware instance 単位で throttle。
 *      各 Store / Feature 内で独立して連打防止する。
 *
 * ---
 *
 * throttle key:
 *
 * Intent class の simpleName 単位。
 *
 * data class の引数差分は考慮しない。
 *
 * 例:
 *
 * NavigateToDetail(id = 1)
 * NavigateToDetail(id = 999)
 *
 * は同じ throttle key として扱われる。
 */
class ThrottleIntentMiddleware<
        US : UiState,
        I : Intent<A>,
        A : Action,
        > : IntentMiddleware<US, I, A> {
    companion object {
        private const val GLOBAL_NAVIGATION_KEY =
            "navigation-throttle"

        /**
         * Navigation 用の global throttle 状態
         *
         * すべての Store / middleware instance 間で共有される。
         */
        private val globalNavigationThrottleState =
            ThrottleState()
    }

    /**
     * Store(local) 単位で利用する throttle 状態
     */
    private val localThrottleState = ThrottleState()

    override suspend fun handleIntent(
        getUiState: () -> US,
        intent: I,
        next: suspend (I) -> Unit,
    ) {
        val throttleIntent = intent as? ThrottleIntent

        // throttle 対象でなければそのまま流す
        if (throttleIntent == null) {
            next(intent)
            return
        }

        val shouldPass = when (throttleIntent.kind) {
            // グローバルとは言っているが、その実は同じインスタンスの共有保持
            ThrottleKind.Navigation -> globalNavigationThrottleState.shouldPass(
                // ListDetailの２Pane表示の際にListとDetailそれぞれでNavigationIntentを発行したときを考え、
                // ミドルウェアではRouteを跨いでナビゲーションだけは判定させている。
                key = GLOBAL_NAVIGATION_KEY,
                throttleMs = throttleIntent.resolvedThrottleMs,
            )

            ThrottleKind.ButtonSpamPrevention -> {
                val key = intent::class.simpleName ?: run {
                    next(intent)
                    return
                }

                localThrottleState.shouldPass(
                    key = key,
                    throttleMs = throttleIntent.resolvedThrottleMs,
                )
            }
        }

        if (shouldPass) {
            next(intent)
            return
        }

        logI("IntentMiddleware") {
            "Skipped by Throttle(${throttleIntent.kind}) Intent: ${
                ObjectFormatter.formatAsSimple(
                    intent
                )
            }"
        }
    }

    /**
     * throttle 判定用状態
     */
    private class ThrottleState {

        private val mutex = Mutex()

        /**
         * throttle key ごとの最終実行時刻
         */
        private val lastExecutionMap =
            ConcurrentHashMap<String, Long>()

        suspend fun shouldPass(
            key: String,
            throttleMs: Long,
        ): Boolean {
            return mutex.withLock {
                val now = SystemClock.elapsedRealtime()
                val lastExecutionTime =
                    lastExecutionMap[key] ?: 0L

                if (now - lastExecutionTime < throttleMs) {
                    false
                } else {
                    lastExecutionMap[key] = now
                    true
                }
            }
        }
    }
}