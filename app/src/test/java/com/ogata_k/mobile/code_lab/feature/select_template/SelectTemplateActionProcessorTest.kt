package com.ogata_k.mobile.code_lab.feature.select_template

import com.ogata_k.mobile.code_lab.core.mvi.CommonMutation
import com.ogata_k.mobile.code_lab.core.mvi.CommonUiEffect
import com.ogata_k.mobile.code_lab.core.mvi.StoreScope
import com.ogata_k.mobile.code_lab.ui.widget.dialog.CommonDialogData
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Test

/**
 * SelectTemplateActionProcessorのテスト
 */
@OptIn(ExperimentalCoroutinesApi::class)
class SelectTemplateActionProcessorTest {
    private val actionProcessor = SelectTemplateActionProcessor()
    private val scope: StoreScope<SelectTemplateUiState, SelectTemplateUiEffect, SelectTemplateIntent, SelectTemplateAction, SelectTemplateMutation> =
        mockk(relaxed = true)

    @Test
    fun `Initializeアクションによって初期化処理が行われること`() = runTest {
        val action = SelectTemplateAction.Initialize

        actionProcessor.process(action, scope)

        // ダイアログの表示、状態更新、スナックバーの表示、ダイアログの置換を確認
        coVerify {
            scope.emitCommonMutation(match { it is CommonMutation.AddDialog && it.data is CommonDialogData.ShowLoading })
            scope.emitMutation(SelectTemplateMutation.ToInitialized)
            scope.emitCommonUiEffect(match { it is CommonUiEffect.ShowSnackbar })
            scope.emitCommonMutation(match { it is CommonMutation.ReplaceDialog && it.data is CommonDialogData.ShowConfirmDialog })
        }
    }

    @Test
    fun `NavigateToTemplateアクションによってNavigateToTemplateエフェクトが発行されること`() =
        runTest {
            val templateDiv = mockk<com.ogata_k.mobile.code_lab.domain.enum.TemplateDiv>()
            val action = SelectTemplateAction.NavigateToTemplate(templateDiv)

            actionProcessor.process(action, scope)

            coVerify { scope.emitUiEffect(SelectTemplateUiEffect.NavigateToTemplate(templateDiv)) }
        }
}
