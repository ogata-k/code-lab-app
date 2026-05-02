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

    protected val intentChannel = Channel<I>(capacity = Channel.BUFFERED)
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
                    { stateManagerScope.getUiStateSnapshot() },
                    currentIntent,
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

    private suspend fun executeIntentPipeline(intent: I) = intentChain(intent)

    // --- Action Pipeline ---

    protected val actionChannel = Channel<A>(capacity = Channel.BUFFERED)

    private var actionLoopJob: Job? = null

    /**
     * Actionの最新のみ結果を残す処理用のJob管理
     */
    private val latestActionJobs = ConcurrentHashMap<ExecutionKey, Job>()

    /**
     * Sequential Actionをキューイングするための専用Channel。
     */
    private val sequentialChannels = ConcurrentHashMap<ExecutionKey, Channel<A>>()
    private val sequentialWorkers = ConcurrentHashMap<ExecutionKey, Job>()

    private val actionMiddlewares: List<ActionMiddleware<US, A>> =
        MviMiddlewareDefaults.defaultActionMiddlewares<US, A>() + additionalActionMiddlewares

    private val actionChain: suspend (A, StateManagerScope<US, UE, M>) -> Unit =
        actionMiddlewares.foldRight<ActionMiddleware<US, A>, suspend (A, StateManagerScope<US, UE, M>) -> Unit>(
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

    /**
     * Actionの唯一の入口。
     */
    fun dispatchAction(action: A) {
        if (actionLoopJob == null) {
            actionLoopJob = scope.launch {
                for (target in actionChannel) {
                    try {
                        executeActionWithStrategy(target)
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

    private suspend fun executeActionPipeline(
        action: A,
        stateManagerScope: StateManagerScope<US, UE, M> = this.stateManagerScope
    ) = actionChain(action, stateManagerScope)

    /**
     * Actionの戦略に応じて実行方法を切り替える
     */
    private suspend fun executeActionWithStrategy(action: A) {
        when (val strategy = action.strategy) {
            ExecutionStrategy.Parallel -> {
                scope.launch {
                    runCatchActionPipeline { executeActionPipeline(action) }
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
                            runCatchActionPipeline { executeActionPipeline(queuedAction) }
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
                            stateManagerScope.ensureActive()
                            stateManagerScope.emitUiEffect(effect)
                        }

                        override suspend fun emitMutation(mutation: M) {
                            stateManagerScope.ensureActive()
                            stateManagerScope.emitMutation(mutation)
                        }
                    }
                    runCatchActionPipeline { executeActionPipeline(action, wrappedScope) }
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

    private suspend inline fun runCatchActionPipeline(crossinline block: suspend () -> Unit) {
        try {
            block()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Throwable) {
            logE("ActionPipeline", e) { "Occurred Unexpected Exception" }
        }
    }

    // --- State & Effect Emitters ---

    protected suspend fun emitUiEffect(effect: UE) {
        logV("StateManager") { "emit UiEffect: $effect" }
        _uiEffect.send(effect)
    }

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

    protected val stateManagerScope = object : StateManagerScope<US, UE, M> {
        override fun getUiStateSnapshot(): US = _uiState.value
        override suspend fun emitUiEffect(effect: UE) = this@BaseStateManager.emitUiEffect(effect)
        override suspend fun emitMutation(mutation: M) =
            this@BaseStateManager.emitMutation(mutation)
    }
}
