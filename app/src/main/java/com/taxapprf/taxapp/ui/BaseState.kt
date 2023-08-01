package com.taxapprf.taxapp.ui

sealed class BaseState {
    object Loading : BaseState()
    data class Error(val t: Throwable) : BaseState()
    object Success : BaseState()
    object SuccessWithEmpty : BaseState()
    object UpdateUI : BaseState()
    object Deleted : BaseState()
    object Edited : BaseState()
    object LogOut : BaseState()
    object AccountSelect : BaseState()
}