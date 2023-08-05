package com.taxapprf.domain.transaction

import com.taxapprf.domain.TransactionType
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

data class SaveTransactionModel(
    var account: String = "",
    var year: String = "",
    var isYearUpdated: Boolean = false,

    var key: String? = null,
    var id: String = "",
    var type: String = "",
    var date: String = "",
    var currency: String = "",
    var rateCentralBank: Double = 0.0,
    var sum: Double = 0.0,
    var sumRub: Double = 0.0,
) {
    private val calendar = Calendar.getInstance()
    private val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.ROOT)

    fun updateTransactionModel(getTransactionModel: TransactionModel) {
        id = getTransactionModel.id
        key = getTransactionModel.key
        date = getTransactionModel.date
        currency = getTransactionModel.currency
        rateCentralBank = getTransactionModel.rateCentralBank
        sum = getTransactionModel.sum
        sumRub = getTransactionModel.sumRub

        updateYearFromDate()
    }


    init {
        year = calendar[Calendar.YEAR].toString()
        date = dateFormat.format(calendar.time).toString()
        type = TransactionType.TRADE.name
    }

    fun updateYear() {
        updateYearFromDate()
        isYearUpdated = true
    }

    private fun updateYearFromDate() {
        if (date != "") {
            val dateSplit = date.split("/")
            year = dateSplit[2]
        }
    }
}