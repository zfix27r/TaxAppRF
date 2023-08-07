package com.taxapprf.domain

data class FirebasePathModel(
    val accountName: String,
    val year: String,
    val transactionKey: String? = null
)