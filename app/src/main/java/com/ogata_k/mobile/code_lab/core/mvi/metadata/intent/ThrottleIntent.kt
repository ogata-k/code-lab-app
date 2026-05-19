package com.ogata_k.mobile.code_lab.core.mvi.metadata.intent

/**
 * Throttleで判定する基準となる種別
 */
enum class ThrottleKind(
    val defaultThrottleMs: Long,
) {
    /**
     * 画面遷移
     */
    Navigation(500),

    /**
     * ボタン連打
     */
    ButtonSpamPrevention(400);
}

/**
 * Throttleを指定するIntentのマーカーインターフェース
 */
interface ThrottleIntent {

    val kind: ThrottleKind

    val throttleMs: Long?
        get() = null

    val resolvedThrottleMs: Long
        get() = throttleMs ?: kind.defaultThrottleMs
}