package com.taxapprf.taxapp.ui.transactions

import com.taxapprf.domain.report.ReportModel
import com.taxapprf.domain.transaction.SaveTransactionModel
import com.taxapprf.domain.transaction.TransactionModel
import com.taxapprf.domain.transaction.TransactionType
import com.taxapprf.taxapp.ui.BaseViewModel
import com.taxapprf.taxapp.ui.error.UIErrorEmailEmpty
import com.taxapprf.taxapp.ui.error.UIErrorEmailIncorrect
import com.taxapprf.taxapp.ui.error.UIErrorNameEmpty
import com.taxapprf.taxapp.ui.error.UIErrorNameTooLong
import com.taxapprf.taxapp.ui.error.UIErrorSumEmpty
import com.taxapprf.taxapp.ui.error.UIErrorSumTooLong
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.LocalDate
import java.time.Year
import java.time.format.DateTimeFormatter
import java.time.format.ResolverStyle
import javax.inject.Inject

@HiltViewModel
class TransactionDetailViewModel @Inject constructor() : BaseViewModel() {
    private val dateFormat = DateTimeFormatter.ofPattern(DATE_PATTERN)

    var report: ReportModel? = null
    var transaction: TransactionModel? = null
        set(value) {
            transactionKey = value?.key
            name = value?.name ?: NAME_DEFAULT
            date = value?.date ?: getCurrentDate()
            type = value?.type ?: TransactionType.TRADE.name
            currency = value?.currency ?: CURRENCY_DEFAULT
            sum = value?.sum ?: SUM_DEFAULT
            field = value
        }

    var transactionKey: String? = null
    var date: String = getCurrentDate()
        set(value) {
            if (value.isEmpty()) error(UIErrorEmailEmpty())
            if (value.isDateCorrect()) field = value
            else error(UIErrorEmailIncorrect())
        }
    var name: String = NAME_DEFAULT
        set(value) {
            if (value.isEmpty()) error(UIErrorNameEmpty())
            else if (value.length > NAME_MAX_LENGTH) error(UIErrorNameTooLong())
            else field = value
        }
    var currency: String = CURRENCY_DEFAULT
    var type: String = TransactionType.TRADE.name
    var sum: Double = SUM_DEFAULT
        set(value) {
            if (value == 0.0) error(UIErrorSumEmpty())
            else if (value > SUM_MAX_LENGTH) error(UIErrorSumTooLong())
            else field = value
        }
    val year
        get() = date.split("/")[2].toInt()

    fun update(year: Int, month: Int, dayOfMonth: Int) {
        date = "$dayOfMonth/$month/$year"
    }

    fun toSaveTransactionModel(): SaveTransactionModel {
        return SaveTransactionModel(
            accountKey = account.name,
            yearKey = year.toString(),
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


    private fun getCurrentDate() = dateFormat.format(LocalDate.now())

    private fun String.isDateCorrect(): Boolean {
        try {
            LocalDate.parse(
                this,
                DateTimeFormatter
                    .ofPattern(DATE_PATTERN)
                    .withResolverStyle(ResolverStyle.STRICT)
            )
        } catch (e: Exception) {
            return false
        }

        if (year < DATE_YEAR_MIN && year > Year.now().value) return false

        return true
    }

    companion object {
        const val NAME_DEFAULT = ""
        const val CURRENCY_DEFAULT = ""
        const val SUM_DEFAULT = 0.0

        const val DATE_YEAR_MIN = 1992
        const val DATE_PATTERN = "dd/MM/yyyy"

        const val NAME_MAX_LENGTH = 16
        const val SUM_MAX_LENGTH = 99999999999
    }
}