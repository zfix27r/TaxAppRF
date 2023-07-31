package com.taxapprf.domain.user

data class UserModel(
    val name: String,
    val email: String,
    val phone: String,
    val accounts: List<AccountModel>,
)