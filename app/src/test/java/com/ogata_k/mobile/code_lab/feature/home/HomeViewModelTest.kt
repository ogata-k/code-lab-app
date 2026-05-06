package com.ogata_k.mobile.code_lab.feature.home

import app.cash.turbine.test
import com.ogata_k.mobile.code_lab.core.mvi.CommonUiEffect
import com.ogata_k.mobile.code_lab.core.mvi.ScreenState
import com.ogata_k.mobile.code_lab.ui.widget.snackbar.CommonSnackbarMessage
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `初期化後にディレイを経てInitialized状態に遷移すること`() = runTest {
        val actionProcessor = HomeActionProcessor()
        // viewModel uses viewModelScope, which uses Dispatchers.Main (set to testDispatcher above)
        val viewModel = HomeViewModel(actionProcessor, mockk())

        viewModel.uiState.test {
            // 1. Initial state from Store
            assertEquals(ScreenState(featureUiState = HomeUiState.UnInitialized), awaitItem())

            // 2. AddDialog(Loading) happens immediately
            val loadingState = awaitItem()
            assertEquals(HomeUiState.UnInitialized, loadingState.featureUiState)
            assert(loadingState.localDialogQueue.isNotEmpty())

            // init block calls Initialize action, which has 1s delay
            advanceTimeBy(1001)

            // 3. ToInitialized によって featureUiState が更新される
            val initializedState = awaitItem()
            assertEquals(HomeUiState.Initialized, initializedState.featureUiState)

            // 4. ReplaceDialog によって localDialogQueue が更新される
            val finalState = awaitItem()
            assertEquals(HomeUiState.Initialized, finalState.featureUiState)
            assert(finalState.localDialogQueue.isNotEmpty())
        }
    }

    @Test
    fun `初期化時にShowSnackbarエフェクトが発行されること`() = runTest {
        val actionProcessor = HomeActionProcessor()
        val viewModel = HomeViewModel(actionProcessor, mockk())

        viewModel.commonUiEffect.test {
            advanceTimeBy(1001)
            val effect = awaitItem()
            assert(effect is CommonUiEffect.ShowSnackbar)
            assertEquals(
                CommonSnackbarMessage.Initialized,
                (effect as CommonUiEffect.ShowSnackbar).data.message
            )
        }
    }

    @Test
    fun `ダイアログを削除できること`() = runTest {
        val actionProcessor = HomeActionProcessor()
        val viewModel = HomeViewModel(actionProcessor, mockk())

        viewModel.uiState.test {
            // 1. 初期状態
            assertEquals(ScreenState(featureUiState = HomeUiState.UnInitialized), awaitItem())

            // 2. ローディング追加
            awaitItem()

            // 3. 初期化完了
            advanceTimeBy(1001)
            awaitItem() // Initialized への遷移
            val stateWithDialog = awaitItem() // ダイアログ置き換え (ConfirmDialog)
            val dialog = stateWithDialog.localDialogQueue.first()

            // 4. ダイアログ削除
            viewModel.removeLocalDialog(dialog)
            val stateEmpty = awaitItem()
            assertEquals(0, stateEmpty.localDialogQueue.size)
        }
    }
}
