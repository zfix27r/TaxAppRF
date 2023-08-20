package com.taxapprf.taxapp.ui.transactions.detail

import com.taxapprf.domain.report.ReportModel
import com.taxapprf.domain.transaction.SaveTransactionModel
import com.taxapprf.domain.transaction.TransactionModel
import com.taxapprf.domain.transaction.TransactionType
import com.taxapprf.taxapp.ui.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class TransactionDetailViewModel @Inject constructor() : BaseViewModel() {
    private val calendar = Calendar.getInstance()
    private val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.ROOT)

    var report: ReportModel? = null
    var transaction: TransactionModel? = null
        set(value) {
            transactionKey = value?.key
            name = value?.name ?: ""
            date = value?.date ?: dateFormat.format(calendar.time)
            type = value?.type ?: TransactionType.TRADE.name
            currency = value?.currency ?: ""
            sum = value?.sum ?: 0.0
            field = value
        }

    var transactionKey: String? = null
    var date: String = dateFormat.format(calendar.time)
    var name: String = ""
    var currency: String = ""
    var type: String = TransactionType.TRADE.name
    var sum: Double = 0.0

    fun update(year: Int, month: Int, dayOfMonth: Int) {
        calendar.set(Calendar.YEAR, year)
        calendar.set(Calendar.MONTH, month)
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

        date = dateFormat.format(calendar.time)
    }

    fun toSaveTransactionModel(): SaveTransactionModel {
        return SaveTransactionModel(
            accountKey = account.name,
            yearKey = date.split("/")[2],
            transactionKey = transaction?.key,
            date = date,
            name = name,
            currency = currency,
            type = type,
            sum = sum
        ).apply {
            report?.let {
                reportYear = it.year
                reportTax = it.tax
                reportSize = it.size
            }
        }
    }
}