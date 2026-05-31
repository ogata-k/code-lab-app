package com.ogata_k.mobile.code_lab.feature.sample_template

import com.ogata_k.mobile.code_lab.core.mvi.StoreScope
import com.ogata_k.mobile.code_lab.ui.widget.dialog.CommonDialogData
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Test

/**
 * SampleTemplateActionProcessorのテスト
 */
class SampleTemplateActionProcessorTest {
    private val actionProcessor = SampleTemplateActionProcessor()
    private val scope: StoreScope<SampleTemplateUiState, SampleTemplateUiEffect, SampleTemplateIntent, SampleTemplateAction, SampleTemplateMutation> =
        mockk(relaxed = true)

    @Test
    fun `DismissDialogアクションによってダイアログが削除されること`() = runTest {
        val dialog = mockk<CommonDialogData>()
        val action = SampleTemplateAction.DismissDialog(dialog)

        actionProcessor.process(action, scope)

        coVerify { scope.removeDialog(dialog) }
    }

    @Test
    fun `NavigateToCounterアクションによってNavigateToCounterエフェクトが発行されること`() =
        runTest {
            val sampleFeatureDiv =
                com.ogata_k.mobile.code_lab.ui.enum.SampleFeatureDiv.CounterSample
            val action = SampleTemplateAction.NavigateToSampleFeature(sampleFeatureDiv)

            actionProcessor.process(action, scope)

            coVerify {
                scope.emitUiEffect(
                    SampleTemplateUiEffect.NavigateToSampleFeature(
                        sampleFeatureDiv
                    )
                )
            }
        }
}
