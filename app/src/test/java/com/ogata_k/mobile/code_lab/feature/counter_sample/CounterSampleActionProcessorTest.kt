package com.ogata_k.mobile.code_lab.feature.counter_sample

import com.ogata_k.mobile.code_lab.core.mvi.StoreScope
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
        val action = CounterSampleAction.Increment(amount)

        actionProcessor.process(action, scope)

        coVerify { scope.emitMutation(CounterSampleMutation.AddCount(amount.toInt())) }
    }

    @Test
    fun `DecrementアクションによってAddCountミューテーションが発行されること`() = runTest {
        val amount = 1u
        val action = CounterSampleAction.Decrement(amount)

        actionProcessor.process(action, scope)

        coVerify { scope.emitMutation(CounterSampleMutation.AddCount(-amount.toInt())) }
    }
}
