package com.ogata_k.mobile.code_lab.core.mvi

import com.ogata_k.mobile.code_lab.common.BuildConfig
import com.ogata_k.mobile.code_lab.global.GlobalUiEffect
import com.ogata_k.mobile.code_lab.ui.widget.dialog.CommonDialogData
import com.ogata_k.mobile.code_lab.ui.widget.snackbar.CommonSnackbarData
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

/**
 * UI状態のマーカーインターフェース
 */
interface UiState

/**
 * すべての画面で共通のUI状態を管理する器
 */
data class ScreenState<US : UiState>(
    val featureUiState: US,
    val localDialogQueue: List<CommonDialogData> = emptyList(),
) {
    /**
     * 共通のローカルダイアログが表示中ならtrue
     */
    fun isInShowLocalDialogQueue(): Boolean {
        return !localDialogQueue.isEmpty()
    }
}

/**
 * UI状態の更新から見たときの副作用のマーカーインターフェース
 */
interface UiEffect

/**
 * 共通で利用するミューテーション
 */
sealed interface CommonUiEffect : UiEffect {
    data class ShowSnackbar(val data: CommonSnackbarData) : CommonUiEffect
}

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
 * 共通で利用するミューテーション
 */
sealed interface CommonMutation : Mutation {
    data class AddDialog(val data: CommonDialogData) : CommonMutation
    data class RemoveDialog(val data: CommonDialogData) : CommonMutation
}

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
 * Store内でアクションの実際の処理を提供するプロセッサーのマーカーインターフェース。
 * UseCaseをハンドリングするInteractorのようなものとなる。
 */
interface ActionProcessor<US : UiState, UE : UiEffect, I : Intent<A>, A : Action, M : Mutation> {
    suspend fun process(action: A, scope: StoreScope<US, UE, I, A, M>)
}

interface Store<US : UiState, UE : UiEffect, I : Intent<A>, A : Action, M : Mutation> {
    /**
     * UI用の状態。
     */
    val uiState: StateFlow<ScreenState<US>>

    /**
     * UI用のサイドエフェクト。
     */
    val uiEffect: Flow<UE>

    /**
     * UI用の共通サイドエフェクト。
     */
    val commonUiEffect: Flow<CommonUiEffect>

    /**
     * Intentを発火
     */
    fun dispatchIntent(intent: I)

    /**
     * Actionを発火
     */
    fun dispatchAction(action: A)
}

/**
 * Store内での処理に必要な機能をまとめたインターフェース
 */
interface StoreScope<US : UiState, UE : UiEffect, I : Intent<A>, A : Action, M : Mutation> {
    fun getUiStateSnapshot(): US

    fun getScreenStateSnapshot(): ScreenState<US>

    suspend fun ensureActive() {
        currentCoroutineContext().ensureActive()
    }

    suspend fun emitUiEffect(effect: UE)

    suspend fun emitCommonUiEffect(effect: CommonUiEffect)

    suspend fun emitGlobalUiEffect(effect: GlobalUiEffect)

    suspend fun emitMutation(mutation: M)

    suspend fun emitCommonMutation(mutation: CommonMutation)

    fun dispatchIntent(intent: I)
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
 * StoreをViewに対して提供し、ライフサイクルを守る「器」となる。
 * 大抵はViewModelがこのインターフェースの実装を担当する。
 */
interface StoreContainer<US : UiState, UE : UiEffect, I : Intent<A>, A : Action> {
    /**
     * UI用の状態。
     */
    val uiState: StateFlow<ScreenState<US>>

    /**
     * UI用のサイドエフェクト。
     */
    val uiEffect: Flow<UE>

    /**
     * UI用のサイドエフェクト。
     */
    val commonUiEffect: Flow<CommonUiEffect>

    /**
     * 利用者の明示的な操作のdispatcher
     */
    fun dispatchIntent(intent: I)

    /**
     * ダイアログを削除
     */
    fun removeLocalDialog(dialog: CommonDialogData)
}
