package com.taxapprf.domain.account

data class AccountModel(
    val account: String,
    val active: Boolean,
    val name: String,
    val email: String,
)