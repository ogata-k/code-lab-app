package com.ogata_k.mobile.code_lab.common

import android.util.Log

/*
 * リリースビルドでは計算すら行われないようにインライン化されたログ関数。
 * ラムダを使用することで、ログが無効な場合に引数の計算（文字列補間など）をスキップします。
 */

/**
 * Log.vのラッパー。リリースビルドでは出力されない。
 * 開発中の詳細な追跡情報など、最も細かいレベルのログに使用。
 *
 * 例：ループの各ステップ、Viewの描画ログ、タッチイベントの生データなど。
 */
inline fun logV(tag: String, throwable: Throwable? = null, message: () -> String) {
    if (BuildConfig.DEBUG) {
        val msg = message()
        if (throwable != null) {
            Log.v(tag, msg, throwable)
        } else {
            Log.v(tag, msg)
        }
    }
}

/**
 * Log.dのラッパー。リリースビルドでは出力されない。
 * デバッグ時に役立つ、プログラムの実行フローを確認するためのログに使用。
 *
 * 例：APIリクエストの開始、DBクエリの実行、ユーザーによるボタンクリックなど。
 */
inline fun logD(tag: String, throwable: Throwable? = null, message: () -> String) {
    if (BuildConfig.DEBUG) {
        val msg = message()
        if (throwable != null) {
            Log.d(tag, msg, throwable)
        } else {
            Log.d(tag, msg)
        }
    }
}

/**
 * Log.iのラッパー。リリースビルドでは出力されない。
 * アプリの主要な状態変化など、一般的な情報ログに使用。
 *
 * 例：ログイン成功、画面遷移の完了、バックグラウンド処理の開始など。
 */
inline fun logI(tag: String, throwable: Throwable? = null, message: () -> String) {
    if (BuildConfig.DEBUG) {
        val msg = message()
        if (throwable != null) {
            Log.i(tag, msg, throwable)
        } else {
            Log.i(tag, msg)
        }
    }
}

/**
 * Log.wのラッパー。リリースビルドでも出力される。
 * 予期しない動作や、エラーではないが注意が必要な警告ログに使用。
 *
 * 例：APIタイムアウト、ネットワーク未接続、ディスク空き容量不足、非推奨APIの使用など。
 */
inline fun logW(tag: String, throwable: Throwable? = null, message: () -> String) {
    val msg = message()
    if (throwable != null) {
        Log.w(tag, msg, throwable)
    } else {
        Log.w(tag, msg)
    }
}

/**
 * Log.eのラッパー。リリースビルドでも出力される。
 * プログラムの実行に支障をきたすようなエラーログに使用。
 *
 * 例：JSONパース失敗、サーバーエラー(500)、データベース破損、NullPointerExceptionなど。
 */
inline fun logE(tag: String, throwable: Throwable? = null, message: () -> String) {
    val msg = message()
    if (throwable != null) {
        Log.e(tag, msg, throwable)
    } else {
        Log.e(tag, msg)
    }
}

/**
 * Log.wtfのラッパー。リリースビルドでも出力される。
 * 決して起こるはずのない深刻なエラー（What a Terrible Failure）に使用。
 *
 * 例：網羅的なwhen文のelseに入った場合、絶対にnullにならないはずの変数がnullの場合など。
 */
inline fun logWtf(tag: String, throwable: Throwable? = null, message: () -> String) {
    val msg = message()
    if (throwable != null) {
        Log.wtf(tag, msg, throwable)
    } else {
        Log.wtf(tag, msg)
    }
}
