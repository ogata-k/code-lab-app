package com.ogata_k.mobile.code_lab.feature.counter_sample

import com.ogata_k.mobile.code_lab.core.mvi.CommonUiEffect
import com.ogata_k.mobile.code_lab.core.mvi.StoreScope
import com.ogata_k.mobile.code_lab.feature.counter_sample.enum.SlideOffsetDivisorType
import com.ogata_k.mobile.code_lab.ui.widget.snackbar.CommonSnackbarData
import com.ogata_k.mobile.code_lab.ui.widget.snackbar.CommonSnackbarMessage
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Test

/**
 * CounterSampleActionProcessorのテスト
 */
class CounterSampleActionProcessorTest {
    private val actionProcessor = CounterSampleActionProcessor()
    private val scope: StoreScope<CounterSampleUiState, CounterSampleUiEffect, CounterSampleIntent, CounterSampleAction, CounterSampleMutation> =
        mockk(relaxed = true)

    @Test
    fun `IncrementアクションによってAddCountミューテーションが発行されること`() = runTest {
        val amount = 1u
        val action = CounterSampleAction.IncrementCount(amount)

        actionProcessor.process(action, scope)

        coVerify { scope.emitMutation(CounterSampleMutation.AddCount(amount.toInt())) }
    }

    @Test
    fun `DecrementアクションによってAddCountミューテーションが発行されること`() = runTest {
        val amount = 1u
        val action = CounterSampleAction.DecrementCount(amount)

        actionProcessor.process(action, scope)

        coVerify { scope.emitMutation(CounterSampleMutation.AddCount(-amount.toInt())) }
    }

    @Test
    fun `UpdateSlideDurationアクションによってSetSlideDurationMsミューテーションが発行されること`() =
        runTest {
            val durationMs = 500u
            val action = CounterSampleAction.UpdateSlideDuration(durationMs)

            actionProcessor.process(action, scope)

            coVerify { scope.emitMutation(CounterSampleMutation.SetSlideDurationMs(durationMs)) }
        }

    @Test
    fun `UpdateSlideDurationアクションで範囲外の値が指定された場合、エラー表示と丸め込みが行われること`() =
        runTest {
            val durationMs = 100u // 最小値120u未満
            val action = CounterSampleAction.UpdateSlideDuration(durationMs)

            actionProcessor.process(action, scope)

            coVerify {
                scope.emitCommonUiEffect(
                    CommonUiEffect.ShowSnackbar(
                        data = CommonSnackbarData(message = CommonSnackbarMessage.ValueOutOfRange)
                    )
                )
                scope.emitMutation(CounterSampleMutation.SetSlideDurationMs(120u))
            }
        }

    @Test
    fun `UpdateFadeDurationアクションによってSetFadeDurationMsミューテーションが発行されること`() =
        runTest {
            val durationMs = 300u
            val action = CounterSampleAction.UpdateFadeDuration(durationMs)

            actionProcessor.process(action, scope)

            coVerify { scope.emitMutation(CounterSampleMutation.SetFadeDurationMs(durationMs)) }
        }

    @Test
    fun `UpdateFadeDurationアクションで範囲外の値が指定された場合、エラー表示と丸め込みが行われること`() =
        runTest {
            val durationMs = 700u // 最大値600u超
            val action = CounterSampleAction.UpdateFadeDuration(durationMs)

            actionProcessor.process(action, scope)

            coVerify {
                scope.emitCommonUiEffect(
                    CommonUiEffect.ShowSnackbar(
                        data = CommonSnackbarData(message = CommonSnackbarMessage.ValueOutOfRange)
                    )
                )
                scope.emitMutation(CounterSampleMutation.SetFadeDurationMs(600u))
            }
        }

    @Test
    fun `UpdateSlideOffsetDivisorアクションによってSetSlideOffsetDivisorミューテーションが発行されること`() =
        runTest {
            val divisorType = SlideOffsetDivisorType.Half
            val action = CounterSampleAction.UpdateSlideOffsetDivisor(divisorType)

            actionProcessor.process(action, scope)

            coVerify { scope.emitMutation(CounterSampleMutation.SetSlideOffsetDivisor(divisorType)) }
        }
}
