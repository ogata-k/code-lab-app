package com.ogata_k.mobile.code_lab.feature.counter_sample

import app.cash.turbine.test
import com.ogata_k.mobile.code_lab.core.mvi.ScreenState
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

/**
 * CounterSampleViewModelのテスト
 */
@OptIn(ExperimentalCoroutinesApi::class)
class CounterSampleViewModelTest {
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
    fun `初期化時にcountが0の状態であること`() = runTest {
        val actionProcessor = CounterSampleActionProcessor()
        val viewModel = CounterSampleViewModel(actionProcessor, mockk())

        viewModel.uiState.test {
            assertEquals(
                ScreenState(featureUiState = CounterSampleUiState(count = 0)),
                awaitItem()
            )
        }
    }
}
