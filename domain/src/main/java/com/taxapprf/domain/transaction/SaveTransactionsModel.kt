package com.taxapprf.domain.transaction

import kotlin.properties.Delegates

data class SaveTransactionsModel(
    val accountKey: String,
    val reportKey: String,
    val transactions: List<SaveTransactionModel>,
) {
/*    lateinit var reportYear: String
    var reportSize by Delegates.notNull<Int>()
    var reportTax by Delegates.notNull<Double>()

    var rateCBR by Delegates.notNull<Double>()
    var tax by Delegates.notNull<Double>()

    fun isReportYearChanged() = reportYear != reportKey
    fun asDeleteTransactionModel() =
        transactionKey?.let {
            DeleteTransactionModel(
                accountKey,
                reportYear,
                transactionKey,
                tax,
                reportSize,
                reportTax
            )
        }

    fun asMapFirebaseTransactionModel(): Map<String, FirebaseTransactionModel> {
        val map = mutableMapOf<String, FirebaseTransactionModel>()
        map {
            map.put(
                it.key, FirebaseTransactionModel(
                    name = it.name,
                    date = it.date,
                    type = it.type,
                    currency = it.currency,
                    rateCBR = it.rateCBR,
                    sum = it.sum,
                    tax = it.tax,
                    syncAt = it.syncAt
                )
            )
        }
        return map
    }*/
}