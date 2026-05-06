package com.ogata_k.mobile.code_lab.feature.home

import app.cash.turbine.test
import com.ogata_k.mobile.code_lab.core.mvi.CommonUiEffect
import com.ogata_k.mobile.code_lab.core.mvi.ScreenState
import com.ogata_k.mobile.code_lab.ui.widget.dialog.CommonDialogData
import com.ogata_k.mobile.code_lab.ui.widget.snackbar.CommonSnackbarMessage
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomeStoreTest {
    @Test
    fun `初期状態がUnInitializedであること`() = runTest {
        val actionProcessor = HomeActionProcessor()
        val store = HomeStore(
            scope = backgroundScope,
            initialState = HomeUiState.UnInitialized,
            actionProcessor = actionProcessor,
            reducer = HomeReducer(),
            globalUiController = mockk()
        )

        assertEquals(ScreenState(featureUiState = HomeUiState.UnInitialized), store.uiState.value)
    }

    @Test
    fun `Initializeアクションによって状態がInitializedに更新されること`() = runTest {
        val actionProcessor = HomeActionProcessor()
        val store = HomeStore(
            scope = backgroundScope,
            initialState = HomeUiState.UnInitialized,
            actionProcessor = actionProcessor,
            reducer = HomeReducer(),
            globalUiController = mockk()
        )

        store.uiState.test {
            // 1. 初期状態
            assertEquals(ScreenState(featureUiState = HomeUiState.UnInitialized), awaitItem())

            store.dispatchAction(HomeAction.Initialize)

            // 2. delay前に AddDialog(ShowLoading) が呼ばれる
            val stateWithLoading = awaitItem()
            assertEquals(HomeUiState.UnInitialized, stateWithLoading.featureUiState)
            assert(stateWithLoading.localDialogQueue.first() is CommonDialogData.ShowLoading)

            advanceTimeBy(1001)

            // 3. ToInitialized によって featureUiState が更新される
            val stateInitialized = awaitItem()
            assertEquals(HomeUiState.Initialized, stateInitialized.featureUiState)
            assert(stateInitialized.localDialogQueue.first() is CommonDialogData.ShowLoading)

            // 4. ReplaceDialog によって localDialogQueue が更新される
            val finalState = awaitItem()
            assertEquals(HomeUiState.Initialized, finalState.featureUiState)
            assert(finalState.localDialogQueue.first() is CommonDialogData.ShowConfirmDialog)
        }
    }

    @Test
    fun `InitializeアクションによってSnackbar表示エフェクトが発行されること`() = runTest {
        val actionProcessor = HomeActionProcessor()
        val store = HomeStore(
            scope = backgroundScope,
            initialState = HomeUiState.UnInitialized,
            actionProcessor = actionProcessor,
            reducer = HomeReducer(),
            globalUiController = mockk()
        )

        store.commonUiEffect.test {
            store.dispatchAction(HomeAction.Initialize)

            advanceTimeBy(1001)
            val effect = awaitItem()
            assert(effect is CommonUiEffect.ShowSnackbar)
            assertEquals(
                CommonSnackbarMessage.Initialized,
                (effect as CommonUiEffect.ShowSnackbar).data.message
            )
        }
    }
}
