package com.ogata_k.mobile.code_lab.feature.counter_sample

import app.cash.turbine.test
import com.ogata_k.mobile.code_lab.core.mvi.ScreenState
import com.ogata_k.mobile.code_lab.feature.counter_sample.enum.SlideOffsetDivisorType
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
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

    @Test
    fun `IncrementCountインテントによってcountが増加すること`() = runTest {
        val actionProcessor = CounterSampleActionProcessor()
        val viewModel = CounterSampleViewModel(actionProcessor, mockk())

        viewModel.uiState.test {
            skipItems(1) // skip initial state

            viewModel.dispatchIntent(CounterSampleIntent.IncrementCount)

            advanceUntilIdle()
            assertEquals(
                ScreenState(featureUiState = CounterSampleUiState(count = 1)),
                awaitItem()
            )
        }
    }

    @Test
    fun `DecrementCountインテントによってcountが減少すること`() = runTest {
        val actionProcessor = CounterSampleActionProcessor()
        val viewModel = CounterSampleViewModel(actionProcessor, mockk())

        viewModel.uiState.test {
            skipItems(1) // skip initial state

            viewModel.dispatchIntent(CounterSampleIntent.DecrementCount)

            advanceUntilIdle()
            assertEquals(
                ScreenState(featureUiState = CounterSampleUiState(count = -1)),
                awaitItem()
            )
        }
    }

    @Test
    fun `UpdateSlideDurationインテントによってslideDurationMsが更新されること`() = runTest {
        val actionProcessor = CounterSampleActionProcessor()
        val viewModel = CounterSampleViewModel(actionProcessor, mockk())

        viewModel.uiState.test {
            skipItems(1) // skip initial state

            viewModel.dispatchIntent(CounterSampleIntent.UpdateSlideDuration(500u))

            advanceUntilIdle()
            assertEquals(
                ScreenState(
                    featureUiState = CounterSampleUiState(
                        count = 0,
                        slideDurationMs = 500u
                    )
                ),
                awaitItem()
            )
        }
    }

    @Test
    fun `UpdateFadeDurationインテントによってfadeDurationMsが更新されること`() = runTest {
        val actionProcessor = CounterSampleActionProcessor()
        val viewModel = CounterSampleViewModel(actionProcessor, mockk())

        viewModel.uiState.test {
            skipItems(1) // skip initial state

            viewModel.dispatchIntent(CounterSampleIntent.UpdateFadeDuration(300u))

            advanceUntilIdle()
            assertEquals(
                ScreenState(
                    featureUiState = CounterSampleUiState(
                        count = 0,
                        fadeDurationMs = 300u
                    )
                ),
                awaitItem()
            )
        }
    }

    @Test
    fun `UpdateSlideOffsetDivisorインテントによってslideOffsetDivisorが更新されること`() = runTest {
        val actionProcessor = CounterSampleActionProcessor()
        val viewModel = CounterSampleViewModel(actionProcessor, mockk())

        viewModel.uiState.test {
            skipItems(1) // skip initial state

            viewModel.dispatchIntent(
                CounterSampleIntent.UpdateSlideOffsetDivisor(
                    SlideOffsetDivisorType.Half
                )
            )

            advanceUntilIdle()
            assertEquals(
                ScreenState(
                    featureUiState = CounterSampleUiState(
                        count = 0,
                        slideOffsetDivisor = SlideOffsetDivisorType.Half
                    )
                ),
                awaitItem()
            )
        }
    }
}
