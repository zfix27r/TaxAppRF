package com.taxapprf.data.remote.firebase.model

import com.taxapprf.domain.transaction.TransactionModel

data class FirebaseTransactionModel(
    val name: String? = null,
    val date: String? = null,
    val type: String? = null,
    val currency: String? = null,
    val rateCBR: Double? = null,
    val sum: Double? = null,
    val tax: Double? = null,
    val syncAt: Long? = null,
) {
    fun toTransactionModel(key: String?): TransactionModel? {
        val transactionKey = key ?: return null
        val name = name
        val date = date ?: return null
        val type = type ?: return null
        val currency = currency ?: return null
        val rateCBR = rateCBR ?: 0.0
        val sum = sum ?: return null
        val tax = tax ?: 0.0
        val syncAt = syncAt ?: 0

        return TransactionModel(
            id = 0,
            transactionKey,
            name,
            date,
            type,
            currency,
            rateCBR,
            sum,
            tax,
            isSync = true,
            isDelete = false,
            syncAt = syncAt
        )
    }
}