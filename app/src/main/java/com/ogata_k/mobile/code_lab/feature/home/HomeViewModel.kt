package com.ogata_k.mobile.code_lab.feature.home
import com.ogata_k.mobile.code_lab.feature.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * Home の ViewModel
 */
@HiltViewModel
class HomeViewModel @Inject constructor(
    stateManager: HomeStateManager
) : BaseViewModel<HomeUiState, HomeUiEffect, HomeIntent, HomeAction, HomeMutation>(
    stateManager = stateManager
) {
    init {
        // 初期データのロード
        dispatchAction(HomeAction.Initialize)
    }
}