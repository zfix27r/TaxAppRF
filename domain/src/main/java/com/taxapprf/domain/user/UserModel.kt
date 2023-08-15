package com.taxapprf.domain.user

import android.net.Uri

data class UserModel(
    val avatar: Uri?,
    val name: String?,
    val email: String?,
    val phone: String?,
)