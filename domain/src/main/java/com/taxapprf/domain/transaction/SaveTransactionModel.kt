package com.taxapprf.domain.transaction

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class SaveTransactionModel {
    private val calendar = Calendar.getInstance()
    private val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.ROOT)

    lateinit var accountName: String
    var transactionKey: String? = null
    var yearOld: String? = null
    var year: String = calendar[Calendar.YEAR].toString()

    var name: String = ""
    var date: String = dateFormat.format(calendar.time)
    var type: String = TransactionType.TRADE.name
    // FIXME поправить, сделать подгрузку из стрингов
    var currency: String = "USD"
    var rateCBR: Long = 0L
    var sum: Long = 0L
    var tax: Long = 0L

    fun from(transactionModel: TransactionModel) {
        transactionKey = transactionModel.key
        name = transactionModel.name
        date = transactionModel.date
        type = transactionModel.type
        currency = transactionModel.currency
        rateCBR = transactionModel.rateCBR
        sum = transactionModel.sum
        tax = transactionModel.tax

        year = date.split("/")[2]
        yearOld = year
    }

    fun updateDate(year: Int, month: Int, dayOfMonth: Int) {
        calendar.set(Calendar.YEAR, year)
        calendar.set(Calendar.MONTH, month)
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

        date = dateFormat.format(calendar.time)
        this.year = year.toString()
    }
}