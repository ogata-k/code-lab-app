package com.ogata_k.mobile.code_lab.feature.counter_sample

import app.cash.turbine.test
import com.ogata_k.mobile.code_lab.core.mvi.CommonUiEffect
import com.ogata_k.mobile.code_lab.core.mvi.ScreenState
import com.ogata_k.mobile.code_lab.feature.counter_sample.enum.SlideOffsetDivisorType
import com.ogata_k.mobile.code_lab.ui.widget.snackbar.CommonSnackbarData
import com.ogata_k.mobile.code_lab.ui.widget.snackbar.CommonSnackbarMessage
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * CounterSampleStoreのテスト
 */
@OptIn(ExperimentalCoroutinesApi::class)
class CounterSampleStoreTest {
    @Test
    fun `初期状態のcountが0であること`() = runTest {
        val actionProcessor = CounterSampleActionProcessor()
        val store = CounterSampleStore(
            scope = backgroundScope,
            initialState = CounterSampleUiState(count = 0),
            actionProcessor = actionProcessor,
            reducer = CounterSampleReducer(),
            globalUiController = mockk()
        )

        assertEquals(
            ScreenState(featureUiState = CounterSampleUiState(count = 0)),
            store.uiState.value
        )
    }

    @Test
    fun `Incrementアクションによってcountがちょうど一つ増加すること`() = runTest {
        val actionProcessor = CounterSampleActionProcessor()
        val store = CounterSampleStore(
            scope = backgroundScope,
            initialState = CounterSampleUiState(count = 0),
            actionProcessor = actionProcessor,
            reducer = CounterSampleReducer(),
            globalUiController = mockk()
        )

        store.uiState.test {
            assertEquals(
                ScreenState(featureUiState = CounterSampleUiState(count = 0)),
                awaitItem()
            )

            store.dispatchAction(CounterSampleAction.IncrementCount(1u))

            advanceUntilIdle()
            assertEquals(
                ScreenState(featureUiState = CounterSampleUiState(count = 1)),
                awaitItem()
            )
        }
    }

    @Test
    fun `Decrementアクションによってcountがちょうど一つ減少すること`() = runTest {
        val actionProcessor = CounterSampleActionProcessor()
        val store = CounterSampleStore(
            scope = backgroundScope,
            initialState = CounterSampleUiState(count = 0),
            actionProcessor = actionProcessor,
            reducer = CounterSampleReducer(),
            globalUiController = mockk()
        )

        store.uiState.test {
            assertEquals(
                ScreenState(featureUiState = CounterSampleUiState(count = 0)),
                awaitItem()
            )

            store.dispatchAction(CounterSampleAction.DecrementCount(1u))

            advanceUntilIdle()
            assertEquals(
                ScreenState(featureUiState = CounterSampleUiState(count = -1)),
                awaitItem()
            )
        }
    }

    @Test
    fun `UpdateSlideDurationアクションによってslideDurationMsが更新されること`() = runTest {
        val actionProcessor = CounterSampleActionProcessor()
        val store = CounterSampleStore(
            scope = backgroundScope,
            initialState = CounterSampleUiState(count = 0, slideDurationMs = 650u),
            actionProcessor = actionProcessor,
            reducer = CounterSampleReducer(),
            globalUiController = mockk()
        )

        store.uiState.test {
            skipItems(1) // skip initial state

            store.dispatchAction(CounterSampleAction.UpdateSlideDuration(500u))

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
    fun `UpdateFadeDurationアクションによってfadeDurationMsが更新されること`() = runTest {
        val actionProcessor = CounterSampleActionProcessor()
        val store = CounterSampleStore(
            scope = backgroundScope,
            initialState = CounterSampleUiState(count = 0, fadeDurationMs = 450u),
            actionProcessor = actionProcessor,
            reducer = CounterSampleReducer(),
            globalUiController = mockk()
        )

        store.uiState.test {
            skipItems(1) // skip initial state

            store.dispatchAction(CounterSampleAction.UpdateFadeDuration(300u))

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
    fun `UpdateSlideOffsetDivisorアクションによってslideOffsetDivisorが更新されること`() = runTest {
        val actionProcessor = CounterSampleActionProcessor()
        val store = CounterSampleStore(
            scope = backgroundScope,
            initialState = CounterSampleUiState(
                count = 0,
                slideOffsetDivisor = SlideOffsetDivisorType.Full
            ),
            actionProcessor = actionProcessor,
            reducer = CounterSampleReducer(),
            globalUiController = mockk()
        )

        store.uiState.test {
            skipItems(1) // skip initial state

            store.dispatchAction(CounterSampleAction.UpdateSlideOffsetDivisor(SlideOffsetDivisorType.Half))

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

    @Test
    fun `UpdateSlideDurationアクションで範囲外の値が指定された場合、共通UIエフェクトが発行されること`() =
        runTest {
            val actionProcessor = CounterSampleActionProcessor()
            val store = CounterSampleStore(
                scope = backgroundScope,
                initialState = CounterSampleUiState(count = 0),
                actionProcessor = actionProcessor,
                reducer = CounterSampleReducer(),
                globalUiController = mockk()
            )

            store.commonUiEffect.test {
                store.dispatchAction(CounterSampleAction.UpdateSlideDuration(100u))

                assertEquals(
                    CommonUiEffect.ShowSnackbar(
                        data = CommonSnackbarData(message = CommonSnackbarMessage.ValueOutOfRange)
                    ),
                    awaitItem()
                )
            }
        }
}
