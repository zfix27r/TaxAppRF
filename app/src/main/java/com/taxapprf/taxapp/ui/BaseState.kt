package com.taxapprf.taxapp.ui

sealed class BaseState

object Loading : BaseState()
data class Error(val t: Throwable) : BaseState()
object Success : BaseState()
object SignOut : BaseState()
object SuccessShare : BaseState()
object SuccessDelete : BaseState()