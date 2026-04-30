package com.ogata_k.mobile.code_lab.core.mvi

import com.ogata_k.mobile.code_lab.common.logV
import com.ogata_k.mobile.code_lab.core.mvi.middleware.MviMiddlewareDefaults
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update

/**
 * Actionの処理と状態更新のパイプラインを管理する基底クラス。
 */
abstract class BaseStateManager<US : UiState, UE : UiEffect, I : Intent<A>, A : Action, M : Mutation>(
    initialState: US,
    private val actionProcessor: ActionProcessor<US, UE, A, M>,
    // ここでReducerを渡すことでreducer自体はSとMからしか計算できない純粋関数みたいなものであることを保証する
    private val reducer: Reducer<US, M>,
    additionalIntentMiddlewares: List<IntentMiddleware<US, I, A>> = emptyList(),
    additionalActionMiddlewares: List<ActionMiddleware<US, A>> = emptyList(),
) {
    private val intentMiddlewares: List<IntentMiddleware<US, I, A>> =
        MviMiddlewareDefaults.defaultIntentMiddlewares<US, I, A>() + additionalIntentMiddlewares

    private val actionMiddlewares: List<ActionMiddleware<US, A>> =
        MviMiddlewareDefaults.defaultActionMiddlewares<US, A>() + additionalActionMiddlewares

    protected val _uiState = MutableStateFlow<US>(initialState)

    /**
     * UI用の状態。UIはこの状態をもとに表示する。
     */
    val uiState: StateFlow<US> = _uiState.asStateFlow()

    protected val _uiEffect = Channel<UE>(Channel.Factory.BUFFERED)

    /**
     * UI用のサイドエフェクト。SharedFlowもいいがナビゲーションにも使うので二重発火をさけるためにもChannelにしている。
     */
    val uiEffect: Flow<UE> = _uiEffect.receiveAsFlow()

    private val stateManagerScope = object : StateManagerScope<US, UE, M> {
        override fun getUiState(): US {
            return _uiState.value
        }

        override suspend fun emitUiEffect(effect: UE) {
            this@BaseStateManager.emitUiEffect(effect)
        }

        override suspend fun emitMutation(mutation: M) {
            this@BaseStateManager.emitMutation(mutation)
        }
    }

    suspend fun executeIntentPipeline(intent: I) {
        // intentをactionに変換してパイプラインに流す処理をnextととして順番に利用できるようにするため、
        // initialをintent->actionとして後ろから畳み込んでnextを構築していく
        val chain = intentMiddlewares.foldRight<IntentMiddleware<US, I, A>, suspend (I) -> Unit>(
            initial = { currentIntent ->
                val action = currentIntent.toAction()
                executeActionPipeline(action)
            }
        ) { middleware, next ->
            { currentIntent ->
                middleware.handle(
                    { -> stateManagerScope.getUiState() },
                    currentIntent,
                    next
                )
            }
        }
        chain(intent)
    }

    suspend fun executeActionPipeline(action: A) {
        // actionを処理するプロセッサーに流す処理をnextととして順番に利用できるようにするため、
        // initialをaction->(()->void)として後ろから畳み込んでnextを構築していく
        val chain = actionMiddlewares.foldRight<ActionMiddleware<US, A>, suspend (A) -> Unit>(
            initial = { currentAction ->
                actionProcessor.process(currentAction, stateManagerScope)
            }
        ) { middleware, next ->
            { currentAction ->
                middleware.handle(
                    { -> stateManagerScope.getUiState() },
                    currentAction,
                    next
                )
            }
        }
        chain(action)
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