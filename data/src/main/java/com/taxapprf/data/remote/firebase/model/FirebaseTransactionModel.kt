package com.taxapprf.data.remote.firebase.model

import com.taxapprf.domain.transaction.SaveTransactionModel

class FirebaseTransactionModel {
    var key: String? = null
    var name: String? = null
    var date: String? = null
    var type: String? = null
    var currency: String? = null
    var rateCBR: Long? = null
    var sum: Long? = null
    var tax: Long? = null

    fun from(saveTransactionModel: SaveTransactionModel): FirebaseTransactionModel {
        key = saveTransactionModel.transactionKey
        name = saveTransactionModel.name
        date = saveTransactionModel.date
        type = saveTransactionModel.type
        currency = saveTransactionModel.currency
        rateCBR = saveTransactionModel.rateCBR
        sum = saveTransactionModel.sum
        tax = saveTransactionModel.tax

        return this
    }
}