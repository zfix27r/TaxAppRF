package com.taxapprf.taxapp.ui

sealed class BaseState {
    object Loading : BaseState()
    data class Error(val t: Throwable) : BaseState()
    object Success : BaseState()
    object SuccessWithEmpty : BaseState()
    object SuccessDelete : BaseState()
    object SuccessEdit : BaseState()
    object LogOut : BaseState()
    object Finish : BaseState()
    object UpdateUI : BaseState()
    object AccountSelect : BaseState()
}