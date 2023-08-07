package com.taxapprf.taxapp.ui

sealed class ActivityBaseState {
    object Loading : ActivityBaseState()
    data class Error(val t: Throwable) : ActivityBaseState()
    object Success : ActivityBaseState()
    object AccountEmpty : ActivityBaseState()
}