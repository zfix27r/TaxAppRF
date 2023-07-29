package com.taxapprf.domain

data class FirebaseRequestModel(
    val account: String,
    val year: String = "",
    val key: String = "",
)