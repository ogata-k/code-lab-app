package com.ogata_k.mobile.code_lab.feature

import kotlinx.coroutines.flow.SharedFlow
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
    fun toAction(): A
}

/**
 * ユーザー操作などによって発生したアクションのマーカーインターフェース
 */
interface Action

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
     * ２回以上呼ばれた時の挙動は保証していません。
     *
     * ミドルウェアでできること:
     * ◦ 継続: next(intent) を呼ぶ。
     * ◦ 中断: next を呼ばない（特定の条件下で処理を止める）。
     * ◦ 変換: next(別のIntent) を呼んで、Intentを書き換える。
     */
    suspend fun handle(
        getUiState: () -> US,
        intent: I,
        next: suspend (I) -> Unit
    )
}

/**
 * Actionを処理する前に実行したい処理、つまりアクション用のミドルウェア。
 * APIなどのアクションの処理にどれくらい時間がかかったか、などログに記録したりするのに使う。
 */
interface ActionMiddleware<US : UiState, A : Action> {
    /**
     * 内部でnextは最大一度だけ呼ぶことができます。
     * ２回以上呼ばれた時の挙動は保証していません。
     *
     * ミドルウェアでできること:
     * ◦ 継続: next(intent) を呼ぶ。
     * ◦ 中断: next を呼ばない（特定の条件下で処理を止める）。
     * ◦ 変換: next(別のIntent) を呼んで、Intentを書き換える。
     */
    suspend fun handle(
        getUiState: () -> US,
        action: A,
        next: suspend (A) -> Unit
    )
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
    fun getUiState(): US
    suspend fun emitUiEffect(effect: UE)
    fun emitMutation(mutation: M)
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
    val uiState: StateFlow<US>
    val uiEffect: SharedFlow<UE>
    fun dispatchIntent(intent: I)
}