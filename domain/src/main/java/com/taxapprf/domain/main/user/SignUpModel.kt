package com.taxapprf.domain.main.user

data class SignUpModel(
    val name: String,
    val email: String,
    val password: String,
    val phone: String
)