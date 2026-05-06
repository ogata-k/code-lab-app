package com.ogata_k.mobile.code_lab.core.mvi

import androidx.lifecycle.ViewModel
import com.ogata_k.mobile.code_lab.ui.widget.dialog.CommonDialogData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

/**
 * featureで利用するViewModelの継承元のモデル
 */
abstract class BaseViewModel<US : UiState, UE : UiEffect, I : Intent<A>, A : Action, M : Mutation> :
    ViewModel(), StoreContainer<US, UE, I, A> {

    protected abstract val store: BaseStore<US, UE, I, A, M>

    override fun onCleared() {
        super.onCleared()
        store.onCleared()
    }

    override val uiState: StateFlow<ScreenState<US>> get() = store.uiState

    override val uiEffect: Flow<UE> get() = store.uiEffect

    override val commonUiEffect: Flow<CommonUiEffect> get() = store.commonUiEffect

    override fun removeLocalDialog(dialog: CommonDialogData) {
        store.removeDialog(dialog)
    }

    override fun replaceLocalDialog(dialog: CommonDialogData, fromData: CommonDialogData?) {
        store.replaceDialog(dialog, fromData)
    }

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
