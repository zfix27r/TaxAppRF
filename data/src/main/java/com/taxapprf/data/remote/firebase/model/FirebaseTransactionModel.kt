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
        val name = name ?: ""
        val date = date ?: return null
        val type = type ?: return null
        val currency = currency ?: return null
        val rateCBR = rateCBR
        val sum = sum ?: return null
        val tax = tax
        val syncAt = syncAt ?: return null

        return TransactionModel(
            transactionKey,
            name,
            date,
            type,
            currency,
            rateCBR,
            sum,
            tax,
            true,
            syncAt
        )
    }
}