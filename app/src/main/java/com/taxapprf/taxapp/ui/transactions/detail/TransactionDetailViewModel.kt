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

    fun checkName(cName: Editable?) = check {
        name = cName.toString()
        if (name.isNameRangeIncorrect()) R.string.transaction_detail_error_name_too_long
        else null
    }

    fun checkDate(cDate: Editable?) = checkDate(cDate.toString())
    fun checkDate(year: Int, month: Int, dayOfMonth: Int): Int? {
        val dayFormatted = if (dayOfMonth < 10) "0$dayOfMonth" else dayOfMonth.toString()
        val monthFormatted = if (month < 10) "0$month" else dayOfMonth.toString()

        return checkDate("$dayFormatted/$monthFormatted/$year")
    }

    fun checkSum(cSum: Editable?) = check {
        sum = 0.0

        try {
            sum = cSum.toString().toDouble()
        } catch (_: java.lang.Exception) {

        }

        if (sum == 0.0) R.string.transaction_detail_error_sum_empty
        else if (sum > SUM_MAX_LENGTH) R.string.transaction_detail_error_sum_too_long
        else null
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

    private fun checkDate(cDate: String) = check {
        date = cDate
        if (date.isDateFormatIncorrect()) R.string.transaction_detail_error_date_format
        else if (date.isDateRangeIncorrect()) R.string.transaction_detail_error_date_range
        else null
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

    private fun String.isNameRangeIncorrect() = length > NAME_MAX_LENGTH

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