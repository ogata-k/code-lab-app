package com.ogata_k.mobile.code_lab.feature

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * featureで利用するViewModelの継承元のモデル
 * ※ Action(A) は StateManager 内部で扱われるため、ViewModel のジェネリクスからは隠蔽している
 */
abstract class BaseViewModel<US : UiState, UE : UiEffect, I : Intent, M : Mutation>(
    initialState: US,
    private val stateManager: BaseStateManager<US, UE, I, *, M>,
    // ここでReducerを渡すことでreducer自体はSとMからしか計算できない純粋関数みたいなものであることを保証する
    private val reducer: Reducer<US, M>,
) : ViewModel(), Store<US, UE, I> {

    private val _uiState = MutableStateFlow(initialState)

    /**
     * UI用の状態。UIはこの状態をもとに表示する。
     */
    override val uiState: StateFlow<US> = _uiState.asStateFlow()

    private val _uiEffects = MutableSharedFlow<UE>()

    /**
     * UI用のサイドエフェクト。一度消費したら保持しないようにSharedFlowになっている。
     */
    override val uiEffects: SharedFlow<UE> = _uiEffects.asSharedFlow()

    private val stateManagerScope = object : StateManagerScope<US, UE, M> {
        override fun getUiState(): US {
            return _uiState.value
        }

        override suspend fun emitUiEffect(effect: UE) {
            this@BaseViewModel.emitUiEffect(effect)
        }

        override fun emitMutation(mutation: M) {
            this@BaseViewModel.emitMutation(mutation)
        }
    }

    /**
     * 利用者の明示的な操作のdispatcher
     */
    override fun dispatch(intent: I) {
        viewModelScope.launch {
            stateManager.executeIntentPipeline(
                intent,
                stateManagerScope,
            )
        }
    }

    // @todo 必要ならEventBusからのイベントをもとにstateManager.executeActionPipelineで処理するリスナーを登録してもいいかも

    protected suspend fun emitUiEffect(effect: UE) {
        _uiEffects.emit(effect)
    }

    protected fun emitMutation(mutation: M) {
        _uiState.update { currentState ->
            reducer.reduce(currentState, mutation)
        }
    }
}
