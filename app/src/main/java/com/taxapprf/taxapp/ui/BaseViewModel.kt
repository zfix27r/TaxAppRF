package com.taxapprf.taxapp.ui

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

open class BaseViewModel @Inject constructor() : ViewModel() {
    private var isLoadingDelayTimeout = false
    private var isLock = false
    val isUnlock
        get() = !isLock

    protected fun startWithLock() {
        isLock = true
        start()
    }

    private fun start() {
        isLoadingDelayTimeout = true

        viewModelScope.launch {
            delay(LOADING_DELAY)
            if (isLoadingDelayTimeout) state.emit(Loading)
        }
    }

    protected fun error(t: Throwable) {
        loaded()
        viewModelScope.launch {
            state.emit(Error(t))
        }
    }

    protected fun success() {
        loaded()
        viewModelScope.launch {
            state.emit(Success)
        }
    }

    protected fun successShare(uri: Uri) {
        loaded()
        viewModelScope.launch {
            state.emit(SuccessShare(uri))
        }
    }

    protected fun successExport(uri: Uri) {
        loaded()
        viewModelScope.launch {
            state.emit(SuccessExport(uri))
        }
    }

    protected fun successImport(uri: Uri) {
        loaded()
        viewModelScope.launch {
            state.emit(SuccessImport(uri))
        }
    }

    private fun loaded() {
        isLock = false
        if (isLoadingDelayTimeout) isLoadingDelayTimeout = false
    }

    companion object {
        private const val LOADING_DELAY = 300L
    }
}