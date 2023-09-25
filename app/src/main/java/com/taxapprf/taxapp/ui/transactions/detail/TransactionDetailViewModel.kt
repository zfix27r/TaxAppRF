package com.taxapprf.taxapp.ui.transactions.detail

import android.text.Editable
import com.taxapprf.domain.report.ReportModel
import com.taxapprf.domain.transaction.SaveTransactionModel
import com.taxapprf.domain.transaction.TransactionModel
import com.taxapprf.domain.transaction.TransactionType
import com.taxapprf.taxapp.R
import com.taxapprf.taxapp.ui.BaseViewModel
import com.taxapprf.taxapp.ui.PATTERN_DATE
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.ResolverStyle
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class TransactionDetailViewModel @Inject constructor() : BaseViewModel() {
    var report: ReportModel? = null
    var transaction: TransactionModel? = null
        set(value) {
            transactionKey = value?.transactionKey
            name = value?.name ?: NAME_DEFAULT
            date = value?.date ?: getCurrentDate()
            type = value?.type ?: TransactionType.TRADE.name
            currency = value?.currency ?: CURRENCY_DEFAULT
            sum = value?.sum ?: SUM_DEFAULT
            field = value
        }

    private var transactionKey: String? = null
    var date: Long = getCurrentDate()

    var name: String = NAME_DEFAULT

    var currency: String = CURRENCY_DEFAULT
    var type: String = TransactionType.TRADE.name
    var sum: Double = SUM_DEFAULT

    private val id
        get() = transaction?.id

    private val tax
        get() = transaction?.let { if (sum == it.sum) it.tax else null }
    private val rateCBRF
        get() = transaction?.let { if (sum == it.sum) it.rateCBRF else null }

    fun checkName(cName: Editable?) = check {
        name = cName.toString()
        if (name.isNameRangeIncorrect()) R.string.transaction_detail_error_name_too_long
        else null
    }

    fun checkDate(cDate: Editable?) = checkDate(cDate.toString())
    fun checkDate(year: Int, month: Int, dayOfMonth: Int): Int? {
        val dayFormatted = if (dayOfMonth < 10) "0$dayOfMonth" else dayOfMonth.toString()
        val monthIncremented = month + 1
        val monthFormatted =
            if (monthIncremented < 10) "0$monthIncremented" else monthIncremented.toString()

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
            id = id,
            accountKey = account.name,
            reportKey = report?.name,
            transactionKey = transaction?.transactionKey,
            newReportKey = date.getYear(),
            date = date,
            name = name,
            currencyCharCode = currency,
            type = type,
            sum = sum,
            tax = tax,
            rateCBRF = rateCBRF
        )
    }

    private fun Long.getYear() = LocalDate.ofEpochDay(this).year.toString()

    private fun checkDate(cDate: String) =
        check {
            cDate.checkDateFormat().let { cDate ->
                if (cDate == null) R.string.transaction_detail_error_date_format
                else {
                    date = cDate
                    if (date.isDateRangeIncorrect()) R.string.transaction_detail_error_date_range
                    else null
                }
            }
        }

    private fun getCurrentDate() = LocalDate.now().toEpochDay()

    private fun String.checkDateFormat() =
        try {
            LocalDate.parse(
                this,
                DateTimeFormatter
                    .ofPattern(PATTERN_DATE)
                    .withResolverStyle(ResolverStyle.STRICT)
            ).toEpochDay()
        } catch (e: Exception) {
            null
        }

    private fun Long.isDateRangeIncorrect() =
        this > LocalDate.now().toEpochDay()

    private fun String.isNameRangeIncorrect() = length > NAME_MAX_LENGTH

    companion object {
        const val NAME_DEFAULT = ""
        const val CURRENCY_DEFAULT = ""
        const val SUM_DEFAULT = 0.0

        const val NAME_MAX_LENGTH = 16
        const val SUM_MAX_LENGTH = 999999999999
    }
}