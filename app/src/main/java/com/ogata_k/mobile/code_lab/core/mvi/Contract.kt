package com.ogata_k.mobile.code_lab.core.mvi

import com.ogata_k.mobile.code_lab.common.BuildConfig
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

/**
 * UI状態のマーカーインターフェース
 */
interface UiState

/**
 * UI状態の更新から見たときの副作用のマーカーインターフェース
 */
interface UiEffect

/**
 * ユーザー操作イベントのマーカーインターフェース
 */
interface Intent<A : Action> {
    fun toAction(): A?
}

/**
 * Actionの実行方法を定義する戦略。
 *
 * 並行性制御の中核となる。
 */
sealed interface ExecutionStrategy {
    /**
     * 並列実行。
     * 他のActionと干渉せず同時に処理される。
     *
     * 例:
     * - ログ送信
     * - 軽いバックグラウンド処理
     */
    object Parallel : ExecutionStrategy

    /**
     * 直列実行。
     * 他のSequentialなActionと排他制御される。
     *
     * 例:
     * - フォーム送信
     * - DB更新
     */
    data class Sequential(val key: ExecutionKey) : ExecutionStrategy

    /**
     * 最新のみ有効（古い処理はキャンセル）。
     *
     * key単位で管理されるため、
     * 異なる機能同士で干渉しない。
     *
     * 例:
     * - 検索
     * - 入力補完
     */
    data class LatestOnly(val key: ExecutionKey) : ExecutionStrategy
}

/**
 * LatestOnlyのスコープを識別するキー。
 *
 * 同じkeyを持つAction同士のみがキャンセル対象になる。
 */
sealed interface ExecutionKey

object DefaultExecutionKey : ExecutionKey

/**
 * ユーザー操作などによって発生したアクションのマーカーインターフェース
 */
interface Action {
    /**
     * 同実行されるべきかという戦略
     */
    val strategy: ExecutionStrategy
}

/**
 * UI状態を更新するミューテーションのマーカーインターフェース
 */
interface Mutation

/**
 * Intentを処理する前に実行したい処理、つまり利用者のUI操作用のミドルウェア。
 * ボタンがいつタップされたか、などアナリティクスをハンドリングするなどに使う。
 */
interface IntentMiddleware<US : UiState, I : Intent<A>, A : Action> {
    /**
     * 内部でnextは最大一度だけ呼ぶことができます。
     * ２回以上呼ばれた時はエラーになります。
     *
     * ミドルウェアでできること:
     * ◦ 継続: next(intent) を呼ぶ。
     * ◦ 中断: next を呼ばない（特定の条件下で処理を止める）。
     * ◦ 変換: next(別のIntent) を呼んで、Intentを書き換える。
     */
    suspend fun handleIntent(
        getUiState: () -> US,
        intent: I,
        next: suspend (I) -> Unit
    )

    suspend fun process(
        getUiState: () -> US,
        intent: I,
        next: suspend (I) -> Unit
    ) {
        var called = false
        handleIntent(getUiState, intent, {
            // 本番でのクラッシュ防止
            if (BuildConfig.DEBUG) {
                check(!called) { "next() called multiple times" }
            }
            called = true
            next(it)
        })
    }
}

/**
 * Actionを処理する前に実行したい処理、つまりアクション用のミドルウェア。
 * APIなどのアクションの処理にどれくらい時間がかかったか、などログに記録したりするのに使う。
 */
interface ActionMiddleware<US : UiState, A : Action> {
    /**
     * 内部でnextは最大一度だけ呼ぶことができます。
     * ２回以上呼ばれた時はエラーになります。
     *
     * ミドルウェアでできること:
     * ◦ 継続: next(intent) を呼ぶ。
     * ◦ 中断: next を呼ばない（特定の条件下で処理を止める）。
     * ◦ 変換: next(別のIntent) を呼んで、Intentを書き換える。
     */
    suspend fun handleAction(
        getUiState: () -> US,
        action: A,
        next: suspend (A) -> Unit
    )

    suspend fun process(
        getUiState: () -> US,
        action: A,
        next: suspend (A) -> Unit
    ) {
        var called = false
        handleAction(getUiState, action, {
            // 本番でのクラッシュ防止
            if (BuildConfig.DEBUG) {
                check(!called) { "next() called multiple times" }
            }
            called = true
            next(it)
        })
    }
}

/**
 * StateManager内でアクションの実際の処理を提供するプロセッサーのマーカーインターフェース。
 * UseCaseをハンドリングするInteractorのようなものとなる。
 */
interface ActionProcessor<US : UiState, UE : UiEffect, A : Action, M : Mutation> {
    suspend fun process(action: A, scope: StateManagerScope<US, UE, M>)
}

/**
 * StateManager内での処理に必要な機能をまとめたインターフェース
 */
interface StateManagerScope<US : UiState, UE : UiEffect, M : Mutation> {
    fun getUiStateSnapshot(): US

    suspend fun ensureActive() {
        currentCoroutineContext().ensureActive()
    }

    suspend fun emitUiEffect(effect: UE)

    suspend fun emitMutation(mutation: M)
}

/**
 * 現在のUI状態をわたってきたmutationをもとに計算するReducer。
 */
interface Reducer<US : UiState, M : Mutation> {
    /**
     * 現在の状態をもとわたってきたmutationを使って新しい状態を計算する純粋関数
     */
    fun reduce(currentState: US, mutation: M): US
}

/**
 * UIに関する状態と操作ディスパッチャーを保持するクラス用のインターフェース。
 * 大抵はViewModelがこのインターフェースの実装を担当する。
 */
interface Store<US : UiState, UE : UiEffect, I : Intent<A>, A : Action> {
    /**
     * UI用の状態。UIはこの状態をもとに表示する。
     */
    val uiState: StateFlow<US>

    /**
     * UI用のサイドエフェクト。SharedFlowもいいがナビゲーションにも使うので二重発火をさけるためにもChannelにしている。
     * そのため、複数の画面でcollectするとどれか一つの画面にしか届かないので注意。
     */
    val uiEffect: Flow<UE>

    /**
     * 利用者の明示的な操作のdispatcher
     */
    fun dispatchIntent(intent: I)
}