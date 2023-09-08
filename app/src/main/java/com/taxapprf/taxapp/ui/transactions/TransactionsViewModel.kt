package com.taxapprf.taxapp.ui.transactions

import android.net.Uri
import androidx.lifecycle.viewModelScope
import com.taxapprf.domain.report.ObserveReportUseCase
import com.taxapprf.domain.report.ReportModel
import com.taxapprf.domain.transaction.DeleteTransactionModel
import com.taxapprf.domain.transaction.DeleteTransactionUseCase
import com.taxapprf.domain.transaction.GetExcelToShareModel
import com.taxapprf.domain.transaction.GetExcelToShareUseCase
import com.taxapprf.domain.transaction.GetExcelToStorageModel
import com.taxapprf.domain.transaction.GetExcelToStorageUseCase
import com.taxapprf.domain.transaction.ObserveTransactionsUseCase
import com.taxapprf.domain.transaction.SaveTransactionModel
import com.taxapprf.domain.transaction.TransactionModel
import com.taxapprf.domain.transaction.UpdateTaxTransactionUseCase
import com.taxapprf.taxapp.ui.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class TransactionsViewModel @Inject constructor(
    private val observeReportUseCase: ObserveReportUseCase,
    private val getTransactionsUseCase: ObserveTransactionsUseCase,
    private val deleteTransactionUseCase: DeleteTransactionUseCase,
    private val getExcelToShareUseCase: GetExcelToShareUseCase,
    private val getExcelToStorageUseCase: GetExcelToStorageUseCase,
    private val updateTaxTransactionUseCase: UpdateTaxTransactionUseCase,
) : BaseViewModel() {
    var report: ReportModel? = null
    private var _transactions: List<TransactionModel> = emptyList()

    fun observeReport(reportKey: String) =
        observeReportUseCase.execute(account.key, reportKey)
            .onStart { start() }
            .onEach {
                success()
                report = it
            }
            .catch { error(it) }
            .flowOn(Dispatchers.IO)

    var deleteTransaction: TransactionModel? = null
    lateinit var excelUri: Uri

    fun observeTransactions(reportKey: String) =
        getTransactionsUseCase.execute(account.key, reportKey)
            .onStart { start() }
            .catch { error(it) }
            .onEach { transactions ->
                success()
                _transactions = transactions
                updateTax()
            }
            .flowOn(Dispatchers.IO)

    fun deleteTransaction() {
        deleteTransactionModel?.let { deleteTransactionModel ->
            viewModelScope.launch(Dispatchers.IO) {
                flow { emit(deleteTransactionUseCase.execute(deleteTransactionModel)) }
                    .onStart { start() }
                    .catch { error(it) }
                    .collectLatest { success() }
            }
        }
    }

    fun getExcelToStorage() = viewModelScope.launch(Dispatchers.IO) {
        report?.let { report ->
            if (_transactions.isNotEmpty()) {
                val getExcelToStorageModel = GetExcelToStorageModel(report, _transactions)
                getExcelToStorageUseCase.execute(getExcelToStorageModel)
                    .onStart { start() }
                    .catch { error(it) }
                    .collectLatest {
                        excelUri = it
                        successExport()
                    }
            }
        }
    }

    fun getExcelToShare() = viewModelScope.launch(Dispatchers.IO) {
        report?.let { report ->
            if (_transactions.isNotEmpty()) {
                val getExcelToShareModel = GetExcelToShareModel(report, _transactions)

                getExcelToShareUseCase.execute(getExcelToShareModel)
                    .onStart { start() }
                    .catch { error(it) }
                    .collectLatest {
                        excelUri = it
                        successShare()
                    }
            }
        }
    }

    private fun updateTax() {
        _transactions.map {
            if (it.rateCBRF == 0.0) {
                it.toSaveTransactionModel()?.let {
                    viewModelScope.launch(Dispatchers.IO) {
                        updateTaxTransactionUseCase.execute(it)
                    }
                }
            }
        }
    }

    private val deleteTransactionModel
        get() =
            report?.let { report ->
                deleteTransaction?.let { transaction ->
                    val deleteModel = DeleteTransactionModel(
                        accountKey = account.key,
                        reportKey = report.key,
                        transactionKey = transaction.key,
                        transactionTax = transaction.tax,
                        reportSize = report.size,
                        reportTax = report.tax
                    )

                    deleteTransaction = null
                    deleteModel
                }
            }

    private fun TransactionModel.toSaveTransactionModel() =
        report?.let {
            SaveTransactionModel(
                id = id,
                accountKey = account.key,
                reportKey = it.key,
                transactionKey = key,
                newReportKey = it.key,
                date = date,
                name = name ?: "",
                currency = currency,
                type = type,
                sum = sum,
            )
        }
}