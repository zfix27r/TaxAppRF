package com.taxapprf.data.remote.firebase.model

import com.taxapprf.data.error.DataErrorResponseEmpty
import com.taxapprf.domain.transaction.TransactionModel

data class FirebaseTransactionModel(
    val name: String? = null,
    val date: String? = null,
    val type: String? = null,
    val currency: String? = null,
    val rateCBR: Double? = null,
    val sum: Double? = null,
    val tax: Double? = null,
) {
    fun toTransactionModel(key: String?) =
        TransactionModel(
            key = key ?: throw DataErrorResponseEmpty(),
            name = name ?: "",
            date = date ?: throw DataErrorResponseEmpty(),
            type = type ?: throw DataErrorResponseEmpty(),
            currency = currency ?: throw DataErrorResponseEmpty(),
            rateCBR = rateCBR ?: throw DataErrorResponseEmpty(),
            sum = sum ?: throw DataErrorResponseEmpty(),
            tax = tax ?: throw DataErrorResponseEmpty()
        )
}