package com.taxapprf.domain.user

data class SaveAccountModel(
    val userName: String,

    val name: String,
    val active: Boolean = true,
)