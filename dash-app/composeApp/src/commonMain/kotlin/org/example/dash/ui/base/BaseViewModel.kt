package org.example.dash.ui.base

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

abstract class BaseViewModel<STATE, INTENT> : ViewModel() {

    private val _state = MutableStateFlow<STATE>(initialState())
    val state: StateFlow<STATE> = _state.asStateFlow()

    abstract fun initialState(): STATE

    abstract fun processIntent(intent: INTENT)

    protected fun updateState(block: (STATE) -> STATE) {
        _state.update(block)
    }

}