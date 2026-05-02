package com.ogata_k.mobile.code_lab.core.mvi

import com.ogata_k.mobile.code_lab.common.global_ui.GlobalUiController
import com.ogata_k.mobile.code_lab.common.global_ui.GlobalUiEffect
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
    private val globalUiController: GlobalUiController,
    additionalIntentMiddlewares: List<IntentMiddleware<US, I, A>> = emptyList(),
    additionalActionMiddlewares: List<ActionMiddleware<US, A>> = emptyList(),
) {
    // --- UI State & Effects ---

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

    // --- Intent Pipeline ---

    /**
     * Intentを直列で呼び出すためのChannel
     */
    protected val intentChannel = Channel<I>(capacity = Channel.BUFFERED)

    /**
     * Intentを直列で呼び出すJob
     */
    private var intentLoopJob: Job? = null

    /**
     * Intentのミドルウェア一覧
     */
    protected val intentMiddlewares: List<IntentMiddleware<US, I, A>> =
        MviMiddlewareDefaults.defaultIntentMiddlewares<US, I, A>() + additionalIntentMiddlewares

    /**
     * Intentのミドルウェアのハンドラー。
     * 一覧で宣言された順で呼び出すようにしている。
     */
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
            { intent ->
                middleware.process(
                    { stateManagerScope.getUiStateSnapshot() },
                    intent,
                    next
                )
            }
        }

    /**
     * Intentの入口。Channel経由で順次処理される。
     */
    fun dispatchIntent(intent: I) {
        if (intentLoopJob == null) {
            intentLoopJob = scope.launch {
                for (target in intentChannel) {
                    executeIntentPipeline(target)
                }
            }
        }

        val result = intentChannel.trySend(intent)
        if (result.isFailure) {
            logE("IntentChannel") { "Failed to dispatch intent: $intent" }
        }
    }

    /**
     * Intentを処理するパイプラインを実行する。
     */
    private suspend fun executeIntentPipeline(intent: I) = intentChain(intent)

    // --- Action Pipeline ---

    /**
     * Actionを直列で呼び出すためのChannel
     */
    protected val actionChannel = Channel<A>(capacity = Channel.BUFFERED)

    /**
     * Actionを直列で呼び出すJob
     */
    private var actionLoopJob: Job? = null

    /**
     * Actionのミドルウェアの一覧
     */
    protected val actionMiddlewares: List<ActionMiddleware<US, A>> =
        MviMiddlewareDefaults.defaultActionMiddlewares<US, A>() + additionalActionMiddlewares

    /**
     * Actionのミドルウェアのハンドラー。
     * 一覧で宣言された順で呼び出すようにしている。
     */
    private val actionChain: suspend (A, StateManagerScope<US, UE, M>) -> Unit =
        actionMiddlewares.foldRight<ActionMiddleware<US, A>, suspend (A, StateManagerScope<US, UE, M>) -> Unit>(
            initial = { currentAction, scope ->
                actionProcessor.process(currentAction, scope)
            }
        ) { middleware, next ->
            { action, scope ->
                middleware.process(
                    { scope.getUiStateSnapshot() },
                    action,
                    { nextAction -> next(nextAction, scope) }
                )
            }
        }

    /**
     * Actionの唯一の入口。
     */
    fun dispatchAction(action: A) {
        if (actionLoopJob == null) {
            actionLoopJob = scope.launch {
                for (target in actionChannel) {
                    try {
                        executeAction(target)
                    } catch (e: CancellationException) {
                        throw e
                    } catch (e: Throwable) {
                        logE(
                            "ActionChannel",
                            e
                        ) { "Critical error in action loop for action: $target" }
                    }
                }
            }
        }

        val result = actionChannel.trySend(action)
        if (result.isFailure) {
            logE("ActionChannel") { "Failed to dispatch action: $action" }
        }
    }

    /**
     * ExecutionStrategy.Sequential戦略で、Actionを直列にするためにキューイングするChannelの管理用Map
     */
    private val sequentialChannels = ConcurrentHashMap<ExecutionKey, Channel<A>>()

    /**
     * ExecutionStrategy.Sequential戦略で、Actionを直列にするためにキューイングするChannel実行のJobの管理用Map
     */
    private val sequentialWorkers = ConcurrentHashMap<ExecutionKey, Job>()

    /**
     * ExecutionStrategy.LatestOnly戦略で、最新のみActionのみを処理するようにしたJobの管理用Map
     */
    private val latestActionJobs = ConcurrentHashMap<ExecutionKey, Job>()

    /**
     * Actionの戦略に応じてActionを処理するパイプラインを実行する。
     */
    private suspend fun executeAction(action: A) {
        when (val strategy = action.strategy) {
            ExecutionStrategy.Parallel -> {
                scope.launch {
                    runCatchActionBlock { actionChain(action, stateManagerScope) }
                }
            }

            is ExecutionStrategy.Sequential -> {
                val key = strategy.key
                val channel = sequentialChannels.getOrPut(key) {
                    Channel(Channel.BUFFERED)
                }

                sequentialWorkers.getOrPut(key) {
                    scope.launch {
                        for (queuedAction in channel) {
                            runCatchActionBlock { actionChain(queuedAction, stateManagerScope) }
                        }
                    }
                }

                channel.send(action)
            }

            is ExecutionStrategy.LatestOnly -> {
                latestActionJobs[strategy.key]?.cancel()

                val job = scope.launch {
                    val wrappedScope = object : StateManagerScope<US, UE, M> {
                        override fun getUiStateSnapshot(): US =
                            stateManagerScope.getUiStateSnapshot()

                        override suspend fun emitUiEffect(effect: UE) {
                            // 最新のみ残す戦略では停止状態になっていれば後続の新しいものがあったということなので、
                            // 途中で止める
                            stateManagerScope.ensureActive()
                            stateManagerScope.emitUiEffect(effect)
                        }

                        override suspend fun emitGlobalUiEffect(effect: GlobalUiEffect) {
                            stateManagerScope.ensureActive()
                            stateManagerScope.emitGlobalUiEffect(effect)
                        }

                        override suspend fun emitMutation(mutation: M) {
                            // 最新のみ残す戦略では停止状態になっていれば後続の新しいものがあったということなので、
                            // 途中で止める
                            stateManagerScope.ensureActive()
                            stateManagerScope.emitMutation(mutation)
                        }
                    }
                    runCatchActionBlock { actionChain(action, wrappedScope) }
                }

                latestActionJobs[strategy.key] = job
                job.invokeOnCompletion {
                    if (latestActionJobs[strategy.key] == job) {
                        latestActionJobs.remove(strategy.key)
                    }
                }
            }
        }
    }

    /**
     * 例外を捕捉しながらActionを実行する。
     */
    private suspend inline fun runCatchActionBlock(crossinline block: suspend () -> Unit) {
        try {
            block()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Throwable) {
            logE("ActionPipeline", e) { "Occurred Unexpected Exception" }
        }
    }

    // --- State & Effect Emitters ---

    /**
     * UI Effectを通知する。
     */
    protected suspend fun emitUiEffect(effect: UE) {
        logV("StateManager") { "emit UiEffect: $effect" }
        _uiEffect.send(effect)
    }

    /**
     * Mutationを適用し、UI Stateを更新する。
     */
    protected suspend fun emitMutation(mutation: M) {
        _uiState.update { currentUiState ->
            val nextUiState = reducer.reduce(currentUiState, mutation)
            logV("StateManager") {
                "emit Mutation: $mutation, from UiState: $currentUiState, to UiState: $nextUiState"
            }
            nextUiState
        }
    }

    // --- Lifecycle ---

    /**
     * 破棄処理。
     * メモリリーク対策として最低限、各種Channelのクローズと実行中のJobのキャンセルを行う。
     */
    fun onCleared() {
        intentChannel.close()
        actionChannel.close()
        sequentialChannels.values.forEach { it.close() }

        intentLoopJob?.cancel()
        actionLoopJob?.cancel()
        sequentialWorkers.values.forEach { it.cancel() }
        latestActionJobs.values.forEach { it.cancel() }

        sequentialChannels.clear()
        sequentialWorkers.clear()
        latestActionJobs.clear()
    }

    // --- Scope Implementation ---

    /**
     * ActionProcessorに渡されるスコープの実装。
     */
    protected val stateManagerScope = object : StateManagerScope<US, UE, M> {
        override fun getUiStateSnapshot(): US = _uiState.value
        override suspend fun emitUiEffect(effect: UE) = this@BaseStateManager.emitUiEffect(effect)
        override suspend fun emitGlobalUiEffect(effect: GlobalUiEffect) =
            this@BaseStateManager.globalUiController.sendUiEffect(effect)

        override suspend fun emitMutation(mutation: M) =
            this@BaseStateManager.emitMutation(mutation)
    }
}
