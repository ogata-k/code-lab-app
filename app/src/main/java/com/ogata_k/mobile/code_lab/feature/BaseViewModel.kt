package com.ogata_k.mobile.code_lab.feature

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * featureで利用するViewModelの継承元のモデル
 */
abstract class BaseViewModel<US : UiState, UE : UiEffect, I : Intent<A>, A : Action, M : Mutation>(
    private val stateManager: BaseStateManager<US, UE, I, A, M>,
) : ViewModel(), Store<US, UE, I, A> {

    /* initで初期データのロードを行う。Action.Initializeという初期化リクエストを発行する
    init {
        // 初期データのロード
        dispatchAction(${featureName}Action.Initialize())
    }
    */

    // @todo 必要ならstateManager.actionProcessorのイベントをここで監視して、必要ならstateManager.executeActionPipelineで処理するリスナーを登録する

    // uiStateは合成して作られているColdフローの可能性があるのでstateInでHotにしておく
    override val uiState: StateFlow<US> = stateManager.uiState.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = stateManager.uiState.value,
    )

    // uiEffectはemitされたUiEffectを流すだけなので合成を使うことは考えられにくいのでデフォルトのまま
    override val uiEffect: SharedFlow<UE> = stateManager.uiEffect

    /**
     * 利用者の明示的な操作のdispatcher
     */
    override fun dispatchIntent(intent: I) {
        viewModelScope.launch {
            stateManager.executeIntentPipeline(intent)
        }
    }

    /**
     * Actionのdispatcher。Storeとしては不要だが、ViewModel内で呼び出すActionを直接発火させるときに使う。
     */
    protected fun dispatchAction(action: A) {
        viewModelScope.launch {
            stateManager.executeActionPipeline(action)
        }
    }
}
