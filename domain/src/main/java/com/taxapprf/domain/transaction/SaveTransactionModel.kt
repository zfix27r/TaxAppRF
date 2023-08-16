package com.taxapprf.domain.transaction

import com.taxapprf.domain.report.ReportModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class SaveTransactionModel {
    private val calendar = Calendar.getInstance()
    private val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.ROOT)


    var accountKey: String? = null
    var yearKey: String? = null
    var transactionKey: String? = null

    var reportTax: Double = 0.0

    var date: String = dateFormat.format(calendar.time)
    var name: String = ""
    var currency: String = ""
    var rateCBR: Double? = null
    var type: String = TransactionType.TRADE.name
    var sum: Double = 0.0
    var tax: Double = 0.0

    fun update(year: Int, month: Int, dayOfMonth: Int) {
        calendar.set(Calendar.YEAR, year)
        calendar.set(Calendar.MONTH, month)
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

        date = dateFormat.format(calendar.time)
    }

    fun update(transactionModel: TransactionModel) {
        transactionKey = transactionModel.key
        name = transactionModel.name
        date = transactionModel.date
        type = transactionModel.type
        currency = transactionModel.currency
        rateCBR = transactionModel.rateCBR
        sum = transactionModel.sum
        tax = transactionModel.tax
    }

    fun update(reportModel: ReportModel) {
        yearKey = reportModel.year
        calendar[Calendar.YEAR] = reportModel.year.toInt()
        date = dateFormat.format(calendar.time)
        reportTax = reportModel.tax
    }
}