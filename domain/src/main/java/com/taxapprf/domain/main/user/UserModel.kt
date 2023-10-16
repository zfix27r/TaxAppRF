package com.taxapprf.domain.main.user

import android.net.Uri

data class UserModel(
    val id: Int,
    val email: String?,
    val avatar: Uri?,
    val name: String?,
    val phone: String?,
)