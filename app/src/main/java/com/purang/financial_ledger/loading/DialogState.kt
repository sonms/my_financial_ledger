package com.purang.financial_ledger.loading

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object DialogState {
    private val _isShowing = MutableStateFlow(false) // 초기값을 false로 설정
    val isShowing: StateFlow<Boolean> = _isShowing.asStateFlow()

    fun show() {
        _isShowing.value = true
    }

    fun hide() {
        _isShowing.value = false
    }
}