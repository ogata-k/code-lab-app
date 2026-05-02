package com.ogata_k.mobile.code_lab

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.ogata_k.mobile.code_lab.common.global_ui.GlobalUiEffect
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * MainActivity全体の状態（ダイアログキューなど）を管理するViewModel。
 * 画面回転時も状態を保持するために使用する。
 */
@HiltViewModel
class MainViewModel @Inject constructor() : ViewModel() {
    // ダイアログをリスト（キュー）で保持
    private val _dialogQueue = mutableStateListOf<GlobalUiEffect>()
    val dialogQueue: List<GlobalUiEffect> get() = _dialogQueue

    fun addDialog(effect: GlobalUiEffect) {
        _dialogQueue.add(effect)
    }

    fun removeDialog(effect: GlobalUiEffect) {
        _dialogQueue.remove(effect)
    }
}
