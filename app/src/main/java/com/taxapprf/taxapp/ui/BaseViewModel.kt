package com.taxapprf.taxapp.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.taxapprf.domain.account.AccountModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

open class BaseViewModel @Inject constructor() : ViewModel() {
    private var isLoadingDelayTimeout = false
    var isLock = false

    private val _state = MutableLiveData<BaseState>()
    val state: LiveData<BaseState> = _state

    lateinit var account: AccountModel

    protected fun startWithLock() {
        isLock = true
        start()
    }

    protected fun start() {
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

    protected fun successShare() {
        loaded()
        _state.postValue(SuccessShare)
    }

    protected fun successExport() {
        loaded()
        _state.postValue(SuccessImport)
    }

    private fun loaded() {
        isLock = false
        if (isLoadingDelayTimeout) isLoadingDelayTimeout = false
    }

    fun check(function: () -> Int?): Int? {
        isLock = true
        return function().apply {
            if (this == null) isLock = false
        }
    }

    companion object {
        private const val LOADING_DELAY = 300L
    }
}