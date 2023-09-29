package com.taxapprf.taxapp.ui

import android.net.Uri

sealed class BaseState

object Loading : BaseState()
data class Error(val t: Throwable) : BaseState()
object Success : BaseState()
object SignOut : BaseState()
data class SuccessShare(val uri: Uri) : BaseState()
data class SuccessImport(val uri: Uri) : BaseState()
object SuccessDelete : BaseState()