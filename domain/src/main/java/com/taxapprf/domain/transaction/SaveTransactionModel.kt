package com.taxapprf.domain.transaction

import kotlin.properties.Delegates

data class SaveTransactionModel(
    val accountKey: String,
    val yearKey: String,
    val transactionKey: String? = null,

    val date: String,
    val name: String,
    val currency: String,
    val type: String,
    val sum: Double,
) {
    lateinit var reportYear: String
    var reportTax by Delegates.notNull<Double>()
    var reportSize by Delegates.notNull<Int>()

    var rateCBR by Delegates.notNull<Double>()
    var tax by Delegates.notNull<Double>()

    fun isReportYearChanged() = reportYear != yearKey
    fun asDeleteTransactionModel() =
        transactionKey?.let {
            DeleteTransactionModel(
                accountKey,
                reportYear,
                transactionKey,
                transactionTax = tax,
                reportTax = reportTax,
                reportSize
            )
        }
}