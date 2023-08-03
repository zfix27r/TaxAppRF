package com.taxapprf.taxapp.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

open class BaseViewModel @Inject constructor() : ViewModel() {
    private var isLoadingDelayTimeout = false

    private val _state = MutableLiveData<BaseState>()
    val state: LiveData<BaseState> = _state

    protected fun loading() {
        isLoadingDelayTimeout = true

        viewModelScope.launch {
            delay(LOADING_DELAY)
            if (isLoadingDelayTimeout) _state.postValue(BaseState.Loading)
        }
    }

    protected fun error(t: Throwable) {
        loaded()
        _state.postValue(BaseState.Error(t))
    }

    protected fun successWithEmpty() {
        loaded()
        _state.postValue(BaseState.Success)
    }

    protected fun success(baseState: BaseState? = null) {
        loaded()
        _state.postValue(baseState ?: BaseState.Success)
    }

    private fun loaded() {
        if (isLoadingDelayTimeout) isLoadingDelayTimeout = false
    }

    companion object {
        private const val LOADING_DELAY = 300L
    }
}