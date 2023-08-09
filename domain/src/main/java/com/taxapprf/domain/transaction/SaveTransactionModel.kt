package com.taxapprf.domain.transaction

import com.taxapprf.domain.report.ReportModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class SaveTransactionModel {
    private val calendar = Calendar.getInstance()
    private val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.ROOT)

    lateinit var accountKey: String
    var transactionKey: String? = null
    var yearOld: String? = null
    var year: String = calendar[Calendar.YEAR].toString()

    var name: String = ""
    var date: String = dateFormat.format(calendar.time)
        set(value) {
            field = value
            year = value.split("/")[2]
        }
    var type: String = TransactionType.TRADE.name

    // FIXME поправить, сделать подгрузку из стрингов
    var currency: String = "USD"
    var rateCBR: Double = 0.0
    var sum: Double = 0.0
    var tax: Double = 0.0

    fun from(transactionModel: TransactionModel) {
        transactionKey = transactionModel.key
        name = transactionModel.name
        date = transactionModel.date
        type = transactionModel.type
        currency = transactionModel.currency
        rateCBR = transactionModel.rateCBR
        sum = transactionModel.sum
        tax = transactionModel.tax

        yearOld = year
    }

    fun from(reportModel: ReportModel) {
        calendar[Calendar.YEAR] = reportModel.year.toInt()
        date = dateFormat.format(calendar.time)
    }

    fun updateDate(year: Int, month: Int, dayOfMonth: Int) {
        calendar.set(Calendar.YEAR, year)
        calendar.set(Calendar.MONTH, month)
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

        date = dateFormat.format(calendar.time)
        this.year = year.toString()
    }
}