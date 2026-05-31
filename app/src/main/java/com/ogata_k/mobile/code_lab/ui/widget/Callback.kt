package com.ogata_k.mobile.code_lab.ui.widget

import androidx.compose.runtime.Stable

// @Stableアノテーションはオブジェクトの状態が変わる可能性があるが、その変化がUIの再描画を必要としない場合に適用される。
@Stable
fun interface UiCallback {
    operator fun invoke(): Unit
}