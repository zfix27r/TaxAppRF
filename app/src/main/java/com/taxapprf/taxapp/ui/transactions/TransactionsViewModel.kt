package com.taxapprf.taxapp.ui.transactions

import android.net.Uri
import androidx.lifecycle.viewModelScope
import com.taxapprf.domain.report.ObserveReportUseCase
import com.taxapprf.domain.report.ReportModel
import com.taxapprf.domain.tax.UpdateTaxUseCase
import com.taxapprf.domain.transaction.DeleteTransactionModel
import com.taxapprf.domain.transaction.DeleteTransactionUseCase
import com.taxapprf.domain.transaction.GetExcelToShareModel
import com.taxapprf.domain.transaction.GetExcelToShareUseCase
import com.taxapprf.domain.transaction.GetExcelToStorageModel
import com.taxapprf.domain.transaction.GetExcelToStorageUseCase
import com.taxapprf.domain.transaction.ObserveTransactionsUseCase
import com.taxapprf.domain.transaction.TransactionModel
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
    private val updateTaxTransactionUseCase: UpdateTaxUseCase,
) : BaseViewModel() {
    lateinit var report: ReportModel
    private var transactions: List<TransactionModel>? = null
    private var isUpdateTaxRun = false

    fun observeReport(reportId: Int) =
        observeReportUseCase.execute(reportId)
            .onStart { start() }
            .catch { error(it) }
            .onEach { success() }
            .flowOn(Dispatchers.IO)

    lateinit var excelUri: Uri

    fun observeTransactions() =
        getTransactionsUseCase.execute(account.id, report.id)
            .onStart { start() }
            .catch { error(it) }
            .onEach {
                success()
                transactions = it
                updateTax()
            }
            .flowOn(Dispatchers.IO)

    fun deleteTransaction(transactionId: Int) = viewModelScope.launch(Dispatchers.IO) {
        val deleteTransactionModel = DeleteTransactionModel(transactionId, report.id)
        deleteTransactionUseCase.execute(deleteTransactionModel)
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

    private fun updateTax() {
        /*        if (!isUpdateTaxRun) {
                    isUpdateTaxRun = true

                    viewModelScope.launch(Dispatchers.IO) {
                        transactions?.map { transaction ->
                            if (transaction.rateCBRF == 0.0)
                                updateTaxTransactionUseCase.execute(transaction.toSaveTransactionModel())
                        }
                        isUpdateTaxRun = false
                    }
                }*/
    }

    /*    private fun TransactionModel.toSaveTransactionModel() =
            SaveTransactionModel(
                id = id,
                accountKey = account.name,
                reportKey = report.name,
                transactionKey = transactionKey,
                newReportKey = report.name,
                date = date,
                name = name ?: "",
                currencyCharCode = currencyCharCode,
                type = type,
                sum = sum,
                tax = tax,
                rateCBRF = rateCBR
            )*/
}