package com.taxapprf.data.remote.firebase.model

import com.taxapprf.data.error.external.DataErrorExternalEmpty
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
            key = key ?: throw DataErrorExternalEmpty(),
            name = name ?: "",
            date = date ?: throw DataErrorExternalEmpty(),
            type = type ?: throw DataErrorExternalEmpty(),
            currency = currency ?: throw DataErrorExternalEmpty(),
            rateCBR = rateCBR ?: throw DataErrorExternalEmpty(),
            sum = sum ?: throw DataErrorExternalEmpty(),
            tax = tax ?: throw DataErrorExternalEmpty()
        )
}