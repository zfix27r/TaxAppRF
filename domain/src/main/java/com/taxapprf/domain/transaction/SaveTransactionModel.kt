package com.taxapprf.domain.transaction

import com.taxapprf.domain.TransactionType
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

data class SaveTransactionModel(
    var key: String? = null,
    var account: String = "",
    var year: String = "",
    var id: String = "",
    var type: String = "",
    var date: String = "",
    var currency: String = "",
    var rateCentralBank: Double = 0.0,
    var sum: Double = 0.0,
    var sumRub: Double = 0.0
) {
    private val calendar = Calendar.getInstance()
    private val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.ROOT)

    init {
        year = calendar[Calendar.YEAR].toString()
        date = dateFormat.format(calendar.time).toString()
        type = TransactionType.TRADE.name
    }
}