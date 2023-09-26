package com.taxapprf.taxapp.ui.transactions.detail

import android.text.Editable
import androidx.lifecycle.viewModelScope
import com.taxapprf.data.getEpochDate
import com.taxapprf.data.local.room.entity.LocalCBRRateEntity.Companion.DEFAULT_CURRENCY_ID
import com.taxapprf.domain.cbr.GetCurrenciesUseCase
import com.taxapprf.domain.report.ReportModel
import com.taxapprf.domain.transaction.SaveTransactionModel
import com.taxapprf.domain.transaction.TransactionModel
import com.taxapprf.domain.transaction.TransactionType
import com.taxapprf.taxapp.R
import com.taxapprf.taxapp.ui.BaseViewModel
import com.taxapprf.taxapp.ui.PATTERN_DATE
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.stateIn
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.ResolverStyle
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class TransactionDetailViewModel @Inject constructor(
    getCurrenciesUseCase: GetCurrenciesUseCase
) : BaseViewModel() {
    private var transactionId: Int? = null
    private var reportId: Int? = null
    private var transactionTax: Double? = null

    var name: String = ""
    var date: Long = getEpochDate()
    var type: Int = TransactionType.TRADE.k
    var currencyId: Int = DEFAULT_CURRENCY_ID
        set(value) {
            field = value + 1
        }
    var sum: Double = 0.0

    fun setFromReportModel(reportModel: ReportModel?) {
        reportModel?.let {
            reportId = it.id
        }
    }

    fun setFromTransactionModel(transactionModel: TransactionModel?) {
        transactionModel?.let { transaction ->
            transactionId = transaction.id
            transaction.tax?.let { transactionTax = it }

            name = transaction.name ?: ""
            date = transaction.date
            type = transaction.type
            currencyId = transaction.currencyId - 1
            sum = transaction.sum
        }
    }

    val currencies =
        getCurrenciesUseCase.execute()
            .flowOn(Dispatchers.IO)
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000L),
                initialValue = null
            )

    val currencyPosition
        get() = currencyId - 1

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
            transactionId = transactionId,
            reportId = reportId,
            accountId = account.id,
            currencyId = currencyId,
            name = name,
            date = date,
            type = type,
            sum = sum,
            tax = transactionTax,
        )
    }

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

    private fun String.checkDateFormat() =
        try {
            LocalDate.parse(
                this,
                DateTimeFormatter
                    .ofPattern(PATTERN_DATE)
                    .withLocale(Locale.ROOT)
                    .withResolverStyle(ResolverStyle.STRICT)
            ).toEpochDay()
        } catch (e: Exception) {
            null
        }

    private fun Long.isDateRangeIncorrect() =
        this > LocalDate.now().toEpochDay()

    private fun String.isNameRangeIncorrect() = length > NAME_MAX_LENGTH

    companion object {
        const val NAME_MAX_LENGTH = 16
        const val SUM_MAX_LENGTH = 999999999999
    }
}