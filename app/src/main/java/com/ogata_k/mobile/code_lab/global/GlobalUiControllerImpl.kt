package com.ogata_k.mobile.code_lab.global

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class GlobalUiControllerImpl : GlobalUiController {
    private val _effects = MutableSharedFlow<GlobalUiEffect>(extraBufferCapacity = 1)
    override val effects: SharedFlow<GlobalUiEffect> = _effects.asSharedFlow()

    override suspend fun sendUiEffect(effect: GlobalUiEffect) {
        _effects.emit(effect)
    }
}
