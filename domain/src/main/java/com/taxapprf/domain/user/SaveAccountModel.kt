package com.taxapprf.domain.user

data class SaveAccountModel(
    val key: String,
    val name: String,
    val active: Boolean = true,
)