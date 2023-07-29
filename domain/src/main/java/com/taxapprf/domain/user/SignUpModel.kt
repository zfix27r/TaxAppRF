package com.taxapprf.domain.user

data class SignUpModel(
    val name: String,
    val email: String,
    val password: String,
    val phone: String
)