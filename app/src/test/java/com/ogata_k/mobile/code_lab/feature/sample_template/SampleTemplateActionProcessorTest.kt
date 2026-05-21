package com.ogata_k.mobile.code_lab.feature.sample_template

import com.ogata_k.mobile.code_lab.core.mvi.StoreScope
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
    fun `NavigateToCounterアクションによってNavigateToCounterエフェクトが発行されること`() =
        runTest {
            val action = SampleTemplateAction.NavigateToSampleFeature

            actionProcessor.process(action, scope)

            coVerify { scope.emitUiEffect(SampleTemplateUiEffect.NavigateToSampleFeature) }
        }
}
