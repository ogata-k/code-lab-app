package com.ogata_k.mobile.code_lab.feature.select_template

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
class SelectTemplateStoreTest {
    @Test
    fun `初期状態がUnInitializedであること`() = runTest {
        val actionProcessor = SelectTemplateActionProcessor()
        val store = SelectTemplateStore(
            scope = backgroundScope,
            initialState = SelectTemplateUiState.UnInitialized,
            actionProcessor = actionProcessor,
            reducer = SelectTemplateReducer(),
            globalUiController = mockk()
        )

        assertEquals(
            ScreenState(featureUiState = SelectTemplateUiState.UnInitialized),
            store.uiState.value
        )
    }

    @Test
    fun `Initializeアクションによって状態がInitializedに更新されること`() = runTest {
        val actionProcessor = SelectTemplateActionProcessor()
        val store = SelectTemplateStore(
            scope = backgroundScope,
            initialState = SelectTemplateUiState.UnInitialized,
            actionProcessor = actionProcessor,
            reducer = SelectTemplateReducer(),
            globalUiController = mockk()
        )

        store.uiState.test {
            // 1. 初期状態
            assertEquals(
                ScreenState(featureUiState = SelectTemplateUiState.UnInitialized),
                awaitItem()
            )

            store.dispatchAction(SelectTemplateAction.Initialize)

            // 2. delay前に AddDialog(ShowLoading) が呼ばれる
            val stateWithLoading = awaitItem()
            assertEquals(SelectTemplateUiState.UnInitialized, stateWithLoading.featureUiState)
            assert(stateWithLoading.localDialogQueue.first() is CommonDialogData.ShowLoading)

            advanceTimeBy(1001)

            // 3. ToInitialized によって featureUiState が更新される
            val stateInitialized = awaitItem()
            assertEquals(SelectTemplateUiState.Initialized, stateInitialized.featureUiState)
            assert(stateInitialized.localDialogQueue.first() is CommonDialogData.ShowLoading)

            // 4. ReplaceDialog によって localDialogQueue が更新される
            val finalState = awaitItem()
            assertEquals(SelectTemplateUiState.Initialized, finalState.featureUiState)
            assert(finalState.localDialogQueue.first() is CommonDialogData.ShowConfirmDialog)
        }
    }

    @Test
    fun `InitializeアクションによってSnackbar表示エフェクトが発行されること`() = runTest {
        val actionProcessor = SelectTemplateActionProcessor()
        val store = SelectTemplateStore(
            scope = backgroundScope,
            initialState = SelectTemplateUiState.UnInitialized,
            actionProcessor = actionProcessor,
            reducer = SelectTemplateReducer(),
            globalUiController = mockk()
        )

        store.commonUiEffect.test {
            store.dispatchAction(SelectTemplateAction.Initialize)

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
