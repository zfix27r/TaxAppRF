package com.taxapprf.domain.user

data class SaveAccountModel(
    val name: String,
    val active: Boolean = true,
)