package com.taxapprf.taxapp.ui

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

open class BaseViewModel @Inject constructor() : ViewModel() {
    private var isLoadingDelayTimeout = false
    private var isLock = false
    val isUnlock
        get() = !isLock

    val state: MutableStateFlow<BaseState> = MutableStateFlow(Loading)

    protected fun startWithLock() {
        isLock = true
        start()
    }

    private fun start() {
        isLoadingDelayTimeout = true

        viewModelScope.launch {
            delay(LOADING_DELAY)
            if (isLoadingDelayTimeout) state.value = Loading
        }
    }

    protected fun error(t: Throwable) {
        loaded()
        state.value = Error(t)
    }

    protected fun success() {
        loaded()
        state.value = Success
    }

    protected fun successShare(uri: Uri) {
        loaded()
        state.value = SuccessShare(uri)
    }

    protected fun successExport(uri: Uri) {
        loaded()
        state.value = SuccessImport(uri)
    }

    protected fun successDelete() {
        state.value = SuccessDelete
    }

    private fun loaded() {
        isLock = false
        if (isLoadingDelayTimeout) isLoadingDelayTimeout = false
    }

    companion object {
        private const val LOADING_DELAY = 300L
    }
}