package com.taxapprf.taxapp.ui.transactions.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.taxapprf.data.ACCOUNT_ID
import com.taxapprf.data.REPORT_ID
import com.taxapprf.data.TRANSACTION_ID
import com.taxapprf.data.getEpochDate
import com.taxapprf.domain.cbr.GetCurrenciesUseCase
import com.taxapprf.domain.report.ObserveReportUseCase
import com.taxapprf.domain.report.ReportModel
import com.taxapprf.domain.toAppDate
import com.taxapprf.domain.transaction.ObserveTransactionUseCase
import com.taxapprf.domain.transaction.SaveTransactionModel
import com.taxapprf.domain.transaction.TransactionModel
import com.taxapprf.taxapp.R
import com.taxapprf.taxapp.ui.BaseViewModel
import com.taxapprf.taxapp.ui.makeHot
import com.taxapprf.taxapp.ui.round
import com.taxapprf.taxapp.ui.toLocalDate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onEach
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class TransactionDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    observeReportUseCase: ObserveReportUseCase,
    observeTransactionUseCase: ObserveTransactionUseCase,
    private val getCurrenciesUseCase: GetCurrenciesUseCase,
) : BaseViewModel() {
    private val accountId = savedStateHandle.get<Int>(ACCOUNT_ID)
    private val reportId = savedStateHandle.get<Int>(REPORT_ID)
        ?.let { if (it == 0) null else it }
    private val transactionId = savedStateHandle.get<Int>(TRANSACTION_ID)
        ?.let { if (it == 0) null else it }

    private var transactionTax: Double? = null

    var name: String = DEFAULT_NAME
    var date: String = getEpochDate().toAppDate()
    var typeK: Int = DEFAULT_TRANSACTION_TYPE_K
    var currency: String = DEFAULT_CURRENCY_NAME
    var sum: String = DEFAULT_SUM

    val report =
        if (transactionId == null) {
            observeReportUseCase.execute(reportId)
                ?.onEach { setFromReportModel(it) }
                ?.makeHot(viewModelScope)
        } else null

    val transaction =
        observeTransactionUseCase.execute(transactionId)
            ?.onEach { setFromTransactionModel(it) }
            ?.makeHot(viewModelScope)

    val currencies =
        flow { emit(getCurrenciesUseCase.execute()) }
            .makeHot(viewModelScope)

    private fun setFromReportModel(reportModel: ReportModel?) {
        reportModel?.name?.let {
            val shiftDate = LocalDate.now().withYear(it.toInt())
            date = shiftDate.toEpochDay().toAppDate()
        }
    }

    private fun setFromTransactionModel(transactionModel: TransactionModel?) {
        transactionModel?.let { transaction ->
            date = transaction.date.toAppDate()
            typeK = transaction.typeK
            sum = transaction.sum.round().toString()

            transaction.tax?.let { transactionTax = it }
            transaction.name?.let { name = it }

            currencies.value
                ?.find { it.id == transaction.currencyId }
                ?.charCode
                ?.let { currency = it }
        }
    }

    fun checkDate(year: Int, month: Int, dayOfMonth: Int): Int? {
        val dayFormatted = if (dayOfMonth < 10) "0$dayOfMonth" else dayOfMonth.toString()
        val monthIncremented = month + 1
        val monthFormatted =
            if (monthIncremented < 10) "0$monthIncremented" else monthIncremented.toString()

        date = "$dayFormatted/$monthFormatted/$year"
        return checkDate()
    }

    fun checkName() =
        if (name.isNameRangeIncorrect()) R.string.transaction_detail_error_name_too_long
        else null

    fun checkDate() =
        date.toLocalDate()?.toEpochDay().let { cDate ->
            if (cDate == null) R.string.transaction_detail_error_date_format
            else if (cDate.isDateRangeIncorrect()) R.string.transaction_detail_error_date_range
            else null
        }

    fun checkSum() =
        try {
            val cSum = sum.toDouble()

            if (cSum > SUM_MAX_LENGTH) R.string.transaction_detail_error_sum_too_long
            else null
        } catch (_: java.lang.Exception) {
            R.string.transaction_detail_error_sum_empty
        }

    fun getSaveTransactionModel(): SaveTransactionModel? {
        val accountId = accountId ?: return null
        val currencyId = currencies.value?.find { it.charCode == currency }?.id ?: return null
        val date = date.toLocalDate()?.toEpochDay() ?: return null

        return SaveTransactionModel(
            transactionId = transactionId,
            reportId = reportId,
            accountId = accountId,
            currencyId = currencyId,
            name = name,
            date = date,
            type = typeK,
            sum = sumToDouble(),
            tax = transactionTax,
        )
    }

    private fun sumToDouble() =
        try {
            sum.toDouble()
        } catch (_: Exception) {
            0.0
        }

    private fun Long.isDateRangeIncorrect() =
        this > LocalDate.now().toEpochDay()

    private fun String.isNameRangeIncorrect() = length > NAME_MAX_LENGTH

    companion object {
        const val NAME_MAX_LENGTH = 30
        const val SUM_MAX_LENGTH = 999999999999

        const val DEFAULT_NAME = ""
        const val DEFAULT_CURRENCY_NAME = "USD"
        const val DEFAULT_TRANSACTION_TYPE_K = 1
        const val DEFAULT_SUM = ""
    }
}