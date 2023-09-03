package com.taxapprf.domain.account

data class AccountModel(
    val accountKey: String,
    val isActive: Boolean = false,
)