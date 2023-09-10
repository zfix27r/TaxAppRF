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
    lateinit var report: ReportModel
    private var transactions: List<TransactionModel>? = null

    fun observeReport(reportKey: String) =
        observeReportUseCase.execute(account.accountKey, reportKey)
            .onStart { start() }
            .catch { error(it) }
            .onEach { success() }
            .flowOn(Dispatchers.IO)

    var deleteTransaction: TransactionModel? = null
    lateinit var excelUri: Uri

    fun observeTransactions() =
        getTransactionsUseCase.execute(account.accountKey, report.reportKey)
            .onStart { start() }
            .catch { error(it) }
            .onEach {
                success()
                transactions = it
                updateTax()
            }
            .flowOn(Dispatchers.IO)

    fun deleteTransaction() {
        deleteTransactionModel?.let { deleteTransactionModel ->
            viewModelScope.launch(Dispatchers.IO) {
                deleteTransactionUseCase.execute(deleteTransactionModel)
            }
        }
    }

    fun getExcelToStorage() = viewModelScope.launch(Dispatchers.IO) {
        transactions?.let { transactions ->
            val getExcelToStorageModel = GetExcelToStorageModel(report, transactions)

            getExcelToStorageUseCase.execute(getExcelToStorageModel)
                .onStart { start() }
                .catch { error(it) }
                .collectLatest {
                    excelUri = it
                    successExport()
                }
        }
    }

    fun getExcelToShare() = viewModelScope.launch(Dispatchers.IO) {
        transactions?.let { transactions ->
            val getExcelToShareModel = GetExcelToShareModel(report, transactions)

            getExcelToShareUseCase.execute(getExcelToShareModel)
                .onStart { start() }
                .catch { error(it) }
                .collectLatest {
                    excelUri = it
                    successShare()
                }
        }
    }

    private val deleteTransactionModel
        get() = deleteTransaction?.let { transaction ->
            val deleteModel = DeleteTransactionModel(
                id = transaction.id,
                accountKey = account.accountKey,
                reportKey = report.reportKey,
                transactionKey = transaction.transactionKey,
                transactionTax = transaction.tax,
                reportSize = report.size,
                reportTax = report.tax
            )

            deleteTransaction = null
            deleteModel
        }

    private fun updateTax() = viewModelScope.launch(Dispatchers.IO) {
        transactions?.map { transaction ->
            if (transaction.rateCBRF == 0.0)
                updateTaxTransactionUseCase.execute(transaction.toSaveTransactionModel())
        }
    }

    private fun TransactionModel.toSaveTransactionModel() =
        SaveTransactionModel(
            id = id,
            accountKey = account.accountKey,
            reportKey = report.reportKey,
            transactionKey = transactionKey,
            newReportKey = report.reportKey,
            date = date,
            name = name ?: "",
            currency = currency,
            type = type,
            sum = sum,
            tax = tax,
            rateCBRF = rateCBRF
        )
}