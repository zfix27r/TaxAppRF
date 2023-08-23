package com.taxapprf.taxapp.ui.transactions.detail

import android.text.Editable
import com.taxapprf.domain.report.ReportModel
import com.taxapprf.domain.transaction.SaveTransactionModel
import com.taxapprf.domain.transaction.TransactionModel
import com.taxapprf.domain.transaction.TransactionType
import com.taxapprf.taxapp.R
import com.taxapprf.taxapp.ui.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.LocalDate
import java.time.Year
import java.time.format.DateTimeFormatter
import java.time.format.ResolverStyle
import java.util.Locale
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

    private var transactionKey: String? = null
    var date: String = getCurrentDate()

    var name: String = NAME_DEFAULT

    var currency: String = CURRENCY_DEFAULT
    var type: String = TransactionType.TRADE.name
    var sum: Double = SUM_DEFAULT

    var hasError = false

    fun checkName(cName: Editable?): Int? {
        hasError = true
        return if (cName != null && cName.length > NAME_MAX_LENGTH)
            R.string.transaction_detail_error_name_too_long
        else {
            hasError = false
            name = cName.toString()
            null
        }
    }

    fun checkDate(cDate: Editable?) = cDate.toString().saveDateOrGetStringMessage()
    fun checkDate(year: Int, month: Int, dayOfMonth: Int) =
        "$dayOfMonth/$month/$year".saveDateOrGetStringMessage()

    fun checkSum(cSum: Editable?): Int? {
        hasError = true
        var checkedSum = 0.0

        cSum?.let {
            if (it.isNotEmpty()) checkedSum = it.toString().toDouble()
        }

        return if (checkedSum == 0.0) R.string.transaction_detail_error_sum_empty
        else if (checkedSum > SUM_MAX_LENGTH) R.string.transaction_detail_error_sum_too_long
        else {
            hasError = false
            sum = checkedSum
            null
        }
    }

    fun getSaveTransactionModel(): SaveTransactionModel {
        return SaveTransactionModel(
            accountKey = account.name,
            yearKey = date.getYear().toString(),
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

    private fun String.getYear() = split("/")[2].toInt()

    private fun String.saveDateOrGetStringMessage(): Int? {
        hasError = true
        return if (isDateRangeIncorrect()) R.string.transaction_detail_error_date_range
        else if (isDateFormatIncorrect()) R.string.transaction_detail_error_date_format
        else {
            hasError = false
            date = this
            null
        }
    }

    private fun getCurrentDate() = dateFormat.format(LocalDate.now())

    private fun String.isDateFormatIncorrect(): Boolean {
        var hasError = false
        try {
            LocalDate.parse(
                this,
                DateTimeFormatter
                    .ofPattern(DATE_PATTERN)
                    .withLocale(Locale.ROOT)
                    .withResolverStyle(ResolverStyle.STRICT)
            )
        } catch (e: Exception) {
            hasError = true
        }
        return hasError
    }

    private fun String.isDateRangeIncorrect(): Boolean {
        val checkYear = getYear()
        return checkYear < DATE_YEAR_MIN || checkYear > Year.now().value
    }

    companion object {
        const val NAME_DEFAULT = ""
        const val CURRENCY_DEFAULT = ""
        const val SUM_DEFAULT = 0.0

        const val DATE_YEAR_MIN = 1992
        const val DATE_PATTERN = "dd/MM/uuuu"

        const val NAME_MAX_LENGTH = 16
        const val SUM_MAX_LENGTH = 999999999999
    }
}