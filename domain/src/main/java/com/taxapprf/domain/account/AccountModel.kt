package com.taxapprf.domain.account


data class AccountModel(
    val id: Int,
    val accountKey: String,
    val isActive: Boolean,
)