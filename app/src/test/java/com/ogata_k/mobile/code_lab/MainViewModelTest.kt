package com.ogata_k.mobile.code_lab

import com.ogata_k.mobile.code_lab.common.global_ui.GlobalUiEffect
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * MainViewModelのテスト
 */
class MainViewModelTest {
    @Test
    fun `初期状態ではダイアログキューが空であること`() {
        val viewModel = MainViewModel()
        assertTrue(viewModel.dialogQueue.isEmpty())
    }

    @Test
    fun `addDialogを呼び出すとキューにエフェクトが追加されること`() {
        val viewModel = MainViewModel()
        val effect = GlobalUiEffect.ShowErrorDialog("Test error") {}

        viewModel.addDialog(effect)

        assertEquals(1, viewModel.dialogQueue.size)
        assertEquals(effect, viewModel.dialogQueue[0])
    }

    @Test
    fun `removeDialogを呼び出すとキューからエフェクトが削除されること`() {
        val viewModel = MainViewModel()
        val effect = GlobalUiEffect.ShowErrorDialog("Test error") {}
        viewModel.addDialog(effect)

        viewModel.removeDialog(effect)

        assertTrue(viewModel.dialogQueue.isEmpty())
    }

    @Test
    fun `複数のダイアログが順番通りに保持されること`() {
        val viewModel = MainViewModel()
        val effect1 = GlobalUiEffect.ShowErrorDialog("Error 1") {}
        val effect2 = GlobalUiEffect.ShowConfirmDialog("Confirm 2") {}

        viewModel.addDialog(effect1)
        viewModel.addDialog(effect2)

        assertEquals(2, viewModel.dialogQueue.size)
        assertEquals(effect1, viewModel.dialogQueue[0])
        assertEquals(effect2, viewModel.dialogQueue[1])
    }
}
