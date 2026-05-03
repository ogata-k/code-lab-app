package com.ogata_k.mobile.code_lab.core.mvi

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

/**
 * featureで利用するViewModelの継承元のモデル
 */
abstract class BaseViewModel<US : UiState, UE : UiEffect, I : Intent<A>, A : Action, M : Mutation> :
    ViewModel(), StoreContainer<US, UE, I, A> {

    protected abstract val store: BaseStore<US, UE, I, A, M>

    /* initで初期データのロードを行う。例：Action.Initializeという初期化リクエストを発行する
    init {
        // 初期データのロード
        dispatchAction(${featureName}Action.Initialize())
    }
    */

    override fun onCleared() {
        super.onCleared()
        store.onCleared()
    }

    override val uiState: StateFlow<US> get() = store.uiState

    // uiEffectはemitされたUiEffectを流すだけなので合成を使うことは考えられにくいのでデフォルトのまま
    override val uiEffect: Flow<UE> get() = store.uiEffect

    /**
     * 利用者の明示的な操作のdispatcher
     */
    override fun dispatchIntent(intent: I) {
        store.dispatchIntent(intent)
    }

    /**
     * Actionのdispatcher。Storeとしては不要だが、ViewModel内で呼び出すActionを直接発火させるときに使う。
     */
    protected fun dispatchAction(action: A) {
        store.dispatchAction(action)
    }
}
