package com.ogata_k.mobile.code_lab.feature.fifteen_puzzle_sample

import com.ogata_k.mobile.code_lab.core.mvi.StoreScope
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Test

/**
 * FifteenPuzzleSampleActionProcessorのテスト
 */
@OptIn(ExperimentalCoroutinesApi::class)
class FifteenPuzzleSampleActionProcessorTest {
    private val actionProcessor = FifteenPuzzleSampleActionProcessor()
    private val scope: StoreScope<FifteenPuzzleSampleUiState, FifteenPuzzleSampleUiEffect, FifteenPuzzleSampleIntent, FifteenPuzzleSampleAction, FifteenPuzzleSampleMutation> =
        mockk(relaxed = true)

    @Test
    fun `InitializeアクションによってToInitializedミューテーションが発行されること`() = runTest {
        val action = FifteenPuzzleSampleAction.Initialize

        actionProcessor.process(action, scope)

        coVerify { scope.emitMutation(FifteenPuzzleSampleMutation.ToInitialized) }
    }
}