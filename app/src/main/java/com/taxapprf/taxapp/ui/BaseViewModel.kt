package com.taxapprf.taxapp.ui

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.taxapprf.domain.user.AccountModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

open class BaseViewModel @Inject constructor() : ViewModel() {
    private var isLoadingDelayTimeout = false
    private var isLock = false
    val isUnlock
        get() = !isLock

    private val _state = MutableLiveData<BaseState>()
    val state: LiveData<BaseState> = _state

    lateinit var account: AccountModel

    protected fun startWithLock() {
        isLock = true
        start()
    }

    private fun start() {
        isLoadingDelayTimeout = true

        viewModelScope.launch {
            delay(LOADING_DELAY)
            if (isLoadingDelayTimeout) _state.postValue(Loading)
        }
    }

    protected fun error(t: Throwable) {
        loaded()
        _state.postValue(Error(t))
    }

    protected fun success() {
        loaded()
        _state.postValue(Success)
    }

    protected fun successShare(uri: Uri) {
        loaded()
        _state.postValue(SuccessShare(uri))
    }

    protected fun successExport(uri: Uri) {
        loaded()
        _state.postValue(SuccessImport(uri))
    }

    protected fun successDelete() {
        _state.postValue(SuccessDelete)
    }

    private fun loaded() {
        isLock = false
        if (isLoadingDelayTimeout) isLoadingDelayTimeout = false
    }

    companion object {
        private const val LOADING_DELAY = 300L
    }
}