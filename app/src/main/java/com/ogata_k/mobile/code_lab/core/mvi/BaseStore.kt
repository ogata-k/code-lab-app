package com.ogata_k.mobile.code_lab.core.mvi

import com.ogata_k.mobile.code_lab.common.logE
import com.ogata_k.mobile.code_lab.common.logI
import com.ogata_k.mobile.code_lab.common.logV
import com.ogata_k.mobile.code_lab.core.mvi.middleware.MviMiddlewareDefaults
import com.ogata_k.mobile.code_lab.global.GlobalUiController
import com.ogata_k.mobile.code_lab.global.GlobalUiEffect
import com.ogata_k.mobile.code_lab.ui.widget.dialog.CommonDialogData
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
abstract class BaseStore<US : UiState, UE : UiEffect, I : Intent<A>, A : Action, M : Mutation>(
    protected val scope: CoroutineScope,
    initialState: US,
    protected val actionProcessor: ActionProcessor<US, UE, I, A, M>,
    // ここでReducerを渡すことでreducer自体はSとMからしか計算できない純粋関数みたいなものであることを保証する
    private val reducer: Reducer<US, M>,
    private val globalUiController: GlobalUiController,
    additionalIntentMiddlewares: List<IntentMiddleware<US, I, A>> = emptyList(),
    additionalActionMiddlewares: List<ActionMiddleware<US, A>> = emptyList(),
) : Store<US, UE, I, A, M> {
    // --- UI State & Effects ---
    protected val _uiState = MutableStateFlow(ScreenState(featureUiState = initialState))

    /**
     * UI用の状態。
     */
    override val uiState: StateFlow<ScreenState<US>> = _uiState.asStateFlow()

    protected val _uiEffect = Channel<UE>(capacity = Channel.BUFFERED)

    /**
     * UI用のサイドエフェクト。SharedFlowもいいがナビゲーションにも使うので二重発火をさけるためにもChannelにしている。
     * そのため、複数の画面でcollectするとどれか一つの画面にしか届かないので注意。
     */
    override val uiEffect: Flow<UE> = _uiEffect.receiveAsFlow()

    protected val _commonUiEffect = Channel<CommonUiEffect>(capacity = Channel.BUFFERED)

    /**
     * 共通のサイドエフェクト。
     */
    override val commonUiEffect: Flow<CommonUiEffect> = _commonUiEffect.receiveAsFlow()

    // --- Scope Implementation ---
    // 他のコンポーネントが依存するため早めに初期化する

    /**
     * ActionProcessorに渡されるスコープの実装。
     */
    protected val storeScope = object : StoreScope<US, UE, I, A, M> {
        override fun getUiStateSnapshot(): US = _uiState.value.featureUiState
        override fun getScreenStateSnapshot(): ScreenState<US> = _uiState.value
        override suspend fun emitUiEffect(effect: UE) {
            this.ensureActive()
            this@BaseStore.emitUiEffect(effect)
        }

        override suspend fun emitCommonUiEffect(effect: CommonUiEffect) {
            this.ensureActive()
            this@BaseStore.emitCommonUiEffect(effect)
        }

        override suspend fun emitGlobalUiEffect(effect: GlobalUiEffect) {
            this.ensureActive()
            this@BaseStore.globalUiController.sendUiEffect(effect)
        }

        override suspend fun emitMutation(mutation: M) {
            this.ensureActive()
            this@BaseStore.emitMutation(mutation)
        }

        override suspend fun emitCommonMutation(mutation: CommonMutation) {
            this.ensureActive()
            this@BaseStore.emitCommonMutation(mutation)
        }

        override fun dispatchIntent(intent: I) = this@BaseStore.dispatchIntent(intent)
    }

    // --- Intent Pipeline ---

    /**
     * Intentを直列で呼び出すためのChannel
     */
    protected val intentChannel = Channel<I>(capacity = Channel.BUFFERED)

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
                    { storeScope.getUiStateSnapshot() },
                    intent,
                    next
                )
            }
        }

    /**
     * Intentの入口。Channel経由で順次処理される。
     */
    override fun dispatchIntent(intent: I) {
        if (intentLoopJob.isCancelled) {
            logI("IntentChannel") { "Canceled to dispatch intent: $intent" }
            return
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
     * Actionのミドルウェアの一覧
     */
    protected val actionMiddlewares: List<ActionMiddleware<US, A>> =
        MviMiddlewareDefaults.defaultActionMiddlewares<US, A>() + additionalActionMiddlewares

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
     * Actionのミドルウェアのハンドラー。
     * 一覧で宣言された順で呼び出すようにしている。
     */
    private val actionChain: suspend (A, StoreScope<US, UE, I, A, M>) -> Unit =
        actionMiddlewares.foldRight<ActionMiddleware<US, A>, suspend (A, StoreScope<US, UE, I, A, M>) -> Unit>(
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
    override fun dispatchAction(action: A) {
        if (actionLoopJob.isCancelled) {
            logI("ActionChannel") { "Canceled dispatch action: $action" }
            return
        }

        val result = actionChannel.trySend(action)
        if (result.isFailure) {
            logE("ActionChannel") { "Failed to dispatch action: $action" }
        }
    }

    /**
     * Actionの戦略に応じてActionを処理するパイプラインを実行する。
     */
    private suspend fun executeAction(action: A) {
        when (val strategy = action.strategy) {
            ExecutionStrategy.Parallel -> {
                scope.launch {
                    runCatchActionBlock { actionChain(action, storeScope) }
                }
            }

            is ExecutionStrategy.Sequential -> {
                val key = strategy.key
                val channel: Channel<A> = sequentialChannels.computeIfAbsent(key) {
                    // UNLIMITEDにすることで、channel.sendでバッファ不足ならずサスペンド（停止）しなくなる
                    Channel(Channel.UNLIMITED)
                }

                sequentialWorkers.computeIfAbsent(key) {
                    scope.launch {
                        for (queuedAction in channel) {
                            runCatchActionBlock { actionChain(queuedAction, storeScope) }
                        }
                    }
                }

                channel.send(action)
            }

            is ExecutionStrategy.LatestOnly -> {
                latestActionJobs.compute(strategy.key) { _, oldJob ->
                    oldJob?.cancel()

                    scope.launch {
                        runCatchActionBlock { actionChain(action, storeScope) }
                    }.also { newJob ->
                        newJob.invokeOnCompletion {
                            // remove(key, value) は「今の値が newJob である場合のみ削除」をアトミックに行う
                            // これにより、後続のActionによって登録された新しいJobを誤って消すことがない
                            latestActionJobs.remove(strategy.key, newJob)
                        }
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

    // --- Main Loop Jobs ---
    // 依存する全てのプロパティ・メソッドが準備できた後に起動する。

    /**
     * Intentを直列で呼び出すJob
     */
    private val intentLoopJob: Job = scope.launch {
        for (target in intentChannel) {
            try {
                executeIntentPipeline(target)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Throwable) {
                logE("IntentChannel", e) { "Failed to process intent: $target" }
            }
        }
    }

    /**
     * Actionを直列で呼び出すJob
     */
    private val actionLoopJob: Job = scope.launch {
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

    // --- State & Effect Emitters ---

    /**
     * UI Effectを通知する。
     */
    protected suspend fun emitUiEffect(effect: UE) {
        logV("Store") { "emit UiEffect: $effect" }
        _uiEffect.send(effect)
    }

    /**
     * 共通のUI Effectを通知する。
     */
    protected suspend fun emitCommonUiEffect(effect: CommonUiEffect) {
        logV("Store") { "emit CommonUiEffect: $effect" }
        _commonUiEffect.send(effect)
    }

    /**
     * Mutationを適用し、UI Stateを更新する。
     */
    protected suspend fun emitMutation(mutation: M) {
        _uiState.update { currentScreenState ->
            val nextFeatureState = reducer.reduce(currentScreenState.featureUiState, mutation)
            logV("Store") {
                "emit Mutation: $mutation, from UiState: ${currentScreenState.featureUiState}, to UiState: $nextFeatureState"
            }
            currentScreenState.copy(featureUiState = nextFeatureState)
        }
    }

    /**
     * 共通のMutationを適用する。
     */
    protected suspend fun emitCommonMutation(mutation: CommonMutation) {
        logV("Store") { "emit CommonMutation: $mutation" }
        _uiState.update { currentScreenState ->
            val nextScreenState = when (mutation) {
                is CommonMutation.AddDialog -> {
                    currentScreenState.copy(localDialogQueue = currentScreenState.localDialogQueue + mutation.data)
                }

                is CommonMutation.RemoveDialog -> {
                    currentScreenState.copy(localDialogQueue = currentScreenState.localDialogQueue - mutation.data)
                }

                is CommonMutation.ReplaceDialog -> {
                    val nextQueue = if (mutation.fromData == null) {
                        if (currentScreenState.localDialogQueue.isNotEmpty()) {
                            currentScreenState.localDialogQueue.toMutableList().apply {
                                set(0, mutation.data)
                            }
                        } else {
                            listOf(mutation.data)
                        }
                    } else {
                        val index = currentScreenState.localDialogQueue.indexOf(mutation.fromData)
                        if (index != -1) {
                            currentScreenState.localDialogQueue.toMutableList().apply {
                                set(index, mutation.data)
                            }
                        } else {
                            listOf(mutation.data) + currentScreenState.localDialogQueue
                        }
                    }
                    currentScreenState.copy(localDialogQueue = nextQueue)
                }
            }
            logV("Store") {
                "emit CommonMutation: $mutation, from ScreenState: $currentScreenState, to ScreenState: $nextScreenState"
            }
            nextScreenState
        }
    }

    /**
     * ダイアログをキューから削除する
     */
    fun removeDialog(dialog: CommonDialogData) {
        scope.launch {
            emitCommonMutation(CommonMutation.RemoveDialog(dialog))
        }
    }

    /**
     * 先頭のダイアログを引数のデータで置き換える。指定されたfromDataがあればそれを置き換える。
     * 先頭がなかったり指定したfromDataが見つからなければ、先頭に追加とする。
     */
    fun replaceDialog(dialog: CommonDialogData, fromData: CommonDialogData? = null) {
        scope.launch {
            emitCommonMutation(CommonMutation.ReplaceDialog(dialog, fromData))
        }
    }

    // --- Lifecycle ---

    /**
     * 破棄処理。
     * メモリリーク対策として最低限、各種Channelのクローズと実行中のJobのキャンセルを行う。
     * scopeがviewModelScopeのような「ViewModelと運命を共にするスコープ」である場合、通常はそのスコープ自体がキャンセルされるため、Jobも自動的にキャンセルされる。
     * 現在の onClearedではscopeがもっと長い寿命を持つ場合も一応想定して「二重の安全策」として保険をかけている。
     */
    fun onCleared() {
        intentChannel.close()
        actionChannel.close()
        _uiEffect.close()
        _commonUiEffect.close()
        sequentialChannels.values.forEach { it.close() }

        intentLoopJob.cancel()
        actionLoopJob.cancel()
        sequentialWorkers.values.forEach { it.cancel() }
        latestActionJobs.values.forEach { it.cancel() }

        sequentialChannels.clear()
        sequentialWorkers.clear()
        latestActionJobs.clear()
    }
}
