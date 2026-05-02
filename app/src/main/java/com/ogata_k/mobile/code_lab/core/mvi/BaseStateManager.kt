package com.ogata_k.mobile.code_lab.core.mvi

import com.ogata_k.mobile.code_lab.common.logE
import com.ogata_k.mobile.code_lab.common.logV
import com.ogata_k.mobile.code_lab.core.mvi.middleware.MviMiddlewareDefaults
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.concurrent.ConcurrentHashMap
import kotlin.coroutines.cancellation.CancellationException

/**
 * Actionの処理と状態更新のパイプラインを管理する基底クラス。
 */
abstract class BaseStateManager<US : UiState, UE : UiEffect, I : Intent<A>, A : Action, M : Mutation>(
    protected val scope: CoroutineScope,
    initialState: US,
    protected val actionProcessor: ActionProcessor<US, UE, A, M>,
    // ここでReducerを渡すことでreducer自体はSとMからしか計算できない純粋関数みたいなものであることを保証する
    private val reducer: Reducer<US, M>,
    additionalIntentMiddlewares: List<IntentMiddleware<US, I, A>> = emptyList(),
    additionalActionMiddlewares: List<ActionMiddleware<US, A>> = emptyList(),
) {
    /**
     * 後かたずけ処理
     */
    fun onCleared() {
        // ① Channel閉じる
        intentChannel.close()
        actionChannel.close()
        sequentialChannels.values.forEach { it.close() }

        // ② Jobキャンセル
        intentLoopJob?.cancel()
        actionLoopJob?.cancel()
        sequentialWorkers.values.forEach { it.cancel() }
        latestActionJobs.values.forEach { it.cancel() }

        // ③ Mapクリア
        sequentialChannels.clear()
        sequentialWorkers.clear()
        latestActionJobs.clear()
    }

    protected val _uiState = MutableStateFlow<US>(initialState)

    /**
     * UI用の状態。UIはこの状態をもとに表示する。
     */
    val uiState: StateFlow<US> = _uiState.asStateFlow()

    protected val _uiEffect = Channel<UE>(capacity = Channel.BUFFERED)

    /**
     * UI用のサイドエフェクト。SharedFlowもいいがナビゲーションにも使うので二重発火をさけるためにもChannelにしている。
     * そのため、複数の画面でcollectするとどれか一つの画面にしか届かないので注意。
     */
    val uiEffect: Flow<UE> = _uiEffect.receiveAsFlow()

    /**
     * すべてのIntentはここを通る。
     */
    private val intentChannel =
        Channel<I>(capacity = Channel.BUFFERED)

    /**
     * dispatchされたIntentの消費Job
     */
    private var intentLoopJob: Job? = null

    private val intentMiddlewares: List<IntentMiddleware<US, I, A>> =
        MviMiddlewareDefaults.defaultIntentMiddlewares<US, I, A>() + additionalIntentMiddlewares

    private val intentChain: suspend (I) -> Unit =
        intentMiddlewares.foldRight<IntentMiddleware<US, I, A>, suspend (I) -> Unit>(
            // intentをactionに変換してパイプラインに流す処理をnextととして順番に利用できるようにするため、
            // initialをintent->actionとして後ろから畳み込んでnextを構築していく
            initial = { currentIntent ->
                val action = currentIntent.toAction()
                if (action != null) {
                    dispatchAction(action)
                }
            }
        ) { middleware, next ->
            { currentIntent ->
                middleware.process(
                    { -> stateManagerScope.getUiStateSnapshot() },
                    currentIntent,
                    next
                )
            }
        }

    private suspend fun executeIntentPipeline(intent: I) = intentChain(intent)

    private val actionMiddlewares: List<ActionMiddleware<US, A>> =
        MviMiddlewareDefaults.defaultActionMiddlewares<US, A>() + additionalActionMiddlewares

    private val actionChain: suspend (A, StateManagerScope<US, UE, M>) -> Unit =
        actionMiddlewares.foldRight<ActionMiddleware<US, A>, suspend (A, StateManagerScope<US, UE, M>) -> Unit>(
            // actionを処理するプロセッサーに流す処理をnextととして順番に利用できるようにするため、
            // initialをaction->(()->void)として後ろから畳み込んでnextを構築していく
            initial = { currentAction, scope ->
                actionProcessor.process(currentAction, scope)
            }
        ) { middleware, next ->
            { currentAction, scope ->
                middleware.process(
                    { scope.getUiStateSnapshot() },
                    currentAction,
                    { nextAction -> next(nextAction, scope) }
                )
            }
        }

    private suspend fun executeActionPipeline(
        action: A,
        stateManagerScope: StateManagerScope<US, UE, M> = this.stateManagerScope
    ) = actionChain(action, stateManagerScope)

    /**
     * すべてのActionはここを通る。
     */
    protected val actionChannel =
        Channel<A>(capacity = Channel.BUFFERED)

    /**
     * dispatchされたActionの消費Job
     */
    private var actionLoopJob: Job? = null

    /**
     * Actionの最新のみ結果を残す処理用のJob管理
     */
    protected val latestActionJobs = ConcurrentHashMap<ExecutionKey, Job>()

    /**
     * Sequential Actionをキューイングするための専用Channel。
     * 送信側をブロックせずに順次キューに追加するために利用。
     */
    private val sequentialChannels =
        ConcurrentHashMap<ExecutionKey, Channel<A>>()

    private val sequentialWorkers =
        ConcurrentHashMap<ExecutionKey, Job>()

    /**
     * Actionの戦略に応じて実行方法を切り替える
     */
    private suspend fun executeActionWithStrategy(action: A) {
        when (val strategy = action.strategy) {
            /**
             * 並列実行
             */
            ExecutionStrategy.Parallel -> {
                scope.launch {
                    try {
                        executeActionPipeline(action)
                    } catch (e: CancellationException) {
                        throw e
                    } catch (e: Throwable) {
                        logE("ActionPipeline", e) { "Occurred Unexpected Exception" }
                    }
                }
            }

            /**
             * 直列実行
             */
            is ExecutionStrategy.Sequential -> {
                val key = strategy.key
                val channel = sequentialChannels.getOrPut(key) {
                    Channel(Channel.BUFFERED)
                }

                // ワーカーを未登録なら登録
                sequentialWorkers.getOrPut(key) {
                    scope.launch {
                        for (action in channel) {
                            try {
                                executeActionPipeline(action)
                            } catch (e: CancellationException) {
                                throw e
                            } catch (e: Throwable) {
                                logE("ActionPipeline", e) { "Occurred Unexpected Exception" }
                            }
                        }
                    }
                }

                channel.send(action)
            }

            /**
             * 最新のみ有効
             */
            is ExecutionStrategy.LatestOnly -> {
                // 同じkeyの前回処理をキャンセル
                latestActionJobs[strategy.key]?.cancel()

                val job = scope.launch {
                    try {
                        executeActionPipeline(
                            action,
                            stateManagerScope = object : StateManagerScope<US, UE, M> {
                                override fun getUiStateSnapshot(): US =
                                    stateManagerScope.getUiStateSnapshot()

                                override suspend fun emitUiEffect(effect: UE) {
                                    // 古いほうがキャンセルされた場合は停止してほしいので無理やりにでもキャンセル処理させる
                                    stateManagerScope.ensureActive()
                                    stateManagerScope.emitUiEffect(effect)
                                }

                                override suspend fun emitMutation(mutation: M) {
                                    // 古いほうがキャンセルされた場合は停止してほしいので無理やりにでもキャンセル処理させる
                                    stateManagerScope.ensureActive()
                                    stateManagerScope.emitMutation(mutation)
                                }
                            }
                        )
                    } catch (e: CancellationException) {
                        throw e
                    } catch (e: Throwable) {
                        logE("ActionPipeline", e) { "Occurred Unexpected Exception" }
                    }
                }

                latestActionJobs[strategy.key] = job

                // 完了時にMapから削除（リーク防止）
                job.invokeOnCompletion {
                    if (latestActionJobs[strategy.key] == job) {
                        latestActionJobs.remove(strategy.key)
                    }
                }
            }
        }
    }

    protected val stateManagerScope = object : StateManagerScope<US, UE, M> {
        override fun getUiStateSnapshot(): US {
            return _uiState.value
        }

        override suspend fun emitUiEffect(effect: UE) {
            this@BaseStateManager.emitUiEffect(effect)
        }

        override suspend fun emitMutation(mutation: M) {
            this@BaseStateManager.emitMutation(mutation)
        }
    }

    /**
     * Intentの入口
     *
     * Channel経由で順次処理される
     */
    fun dispatchIntent(intent: I) {
        // Intentの単一消費用のリスナーの登録がまだなら登録
        if (intentLoopJob == null) {
            intentLoopJob = scope.launch {
                for (intent in intentChannel) {
                    executeIntentPipeline(intent)
                }
            }
        }

        val result = intentChannel.trySend(intent)
        if (result.isFailure) {
            logE("IntentChannel") { "Failed to dispatch intent: $intent" }
        }
    }

    /**
     * Actionの唯一の入口
     */
    fun dispatchAction(action: A) {
        // Actionの単一消費用のリスナーの登録がまだなら登録
        if (actionLoopJob == null) {
            actionLoopJob = scope.launch {
                for (action in actionChannel) {
                    try {
                        executeActionWithStrategy(action)
                    } catch (e: CancellationException) {
                        // コルーチンのキャンセルは再スローしてループを正しく終了させる
                        throw e
                    } catch (e: Throwable) {
                        // ここでキャッチすることで、メインループは生き残り続ける
                        logE(
                            "ActionChannel",
                            e
                        ) { "Critical error in action loop for action: $action" }
                    }
                }
            }
        }

        val result = actionChannel.trySend(action)
        if (result.isFailure) {
            logE("ActionChannel") { "Failed to dispatch action: $action" }
        }
    }

    protected suspend fun emitUiEffect(effect: UE) {
        logV("StateManager") { "emit UiEffect: $effect" }
        _uiEffect.send(effect)
    }

    protected suspend fun emitMutation(mutation: M) {
        _uiState.update { currentUiState ->
            val nextUiState = reducer.reduce(currentUiState, mutation)
            logV(
                "StateManager"
            ) {
                "emit Mutation: $mutation, from UiState: $currentUiState, to UiState: $nextUiState"
            }
            nextUiState
        }
    }
}