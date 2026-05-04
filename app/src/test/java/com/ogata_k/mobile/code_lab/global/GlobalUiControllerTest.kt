package com.ogata_k.mobile.code_lab.global

import app.cash.turbine.test
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.test.runTest
import org.junit.Test

class GlobalUiControllerTest {
    @Test
    fun `配信されたグローバルなUIイベントが確認できること`() = runTest {
        val controller = GlobalUiControllerImpl()
        val expected =
            GlobalUiEffect.ShowCriticalAlertSnackbar(GlobalUiEffectMessage.UnexpectedError)

        // Turbine を使って、送信前に「待ち受け」を開始する
        controller.effects.test {
            controller.sendUiEffect(expected)
            assertEquals(expected, awaitItem())
        }
    }
}