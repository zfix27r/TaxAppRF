package com.taxapprf.taxapp.ui.transactions

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.taxapprf.domain.report.ObserveReportModel
import com.taxapprf.domain.report.ObserveReportUseCase
import com.taxapprf.domain.report.ReportModel
import com.taxapprf.domain.transaction.DeleteTransactionModel
import com.taxapprf.domain.transaction.DeleteTransactionUseCase
import com.taxapprf.domain.transaction.GetExcelToShareModel
import com.taxapprf.domain.transaction.GetExcelToShareUseCase
import com.taxapprf.domain.transaction.GetExcelToStorageModel
import com.taxapprf.domain.transaction.GetExcelToStorageUseCase
import com.taxapprf.domain.transaction.ObserveTransactionsModel
import com.taxapprf.domain.transaction.ObserveTransactionsUseCase
import com.taxapprf.domain.transaction.TransactionModel
import com.taxapprf.taxapp.ui.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
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
) : BaseViewModel() {
    private var reportSize = 0

    lateinit var report: ReportModel

    fun observeReport(yearKey: String): Flow<List<ReportModel>> {
        val observeReportModel = ObserveReportModel(account.key, yearKey)
        return observeReportUseCase.execute(observeReportModel)
            .onStart { start() }
            .catch { error(it) }
            .onEach { success() }
            .flowOn(Dispatchers.IO)
    }

    private val _transactions = MutableLiveData<List<TransactionModel>>()
    val transactions: LiveData<List<TransactionModel>> = _transactions

    var deleteTransaction: TransactionModel? = null
    lateinit var excelUri: Uri

    fun loadTransactions() = viewModelScope.launch(Dispatchers.IO) {
        val getTransactionsModel = ObserveTransactionsModel(account.key, report.key)
        getTransactionsUseCase.execute(getTransactionsModel)
            .onStart { start() }
            .catch { error(it) }
            .onEach { reportSize = it.size }
            .collectLatest {
                _transactions.postValue(it)
                success()
            }
    }

    fun deleteTransaction() {
        deleteTransactionModel?.let { deleteTransactionModel ->
            viewModelScope.launch(Dispatchers.IO) {
                deleteTransactionUseCase.execute(deleteTransactionModel)
                    .onStart { start() }
                    .catch { error(it) }
                    .collectLatest { success() }
            }
        }
    }

    fun onSwipedTransaction(position: Int) {
        deleteTransaction = _transactions.value?.get(position)
    }


    fun getExcelToStorage() = viewModelScope.launch(Dispatchers.IO) {
        transactions.value?.let { transactions ->
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
        transactions.value?.let { transactions ->
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
                accountKey = account.key,
                reportKey = report.key,
                transactionKey = transaction.key,
                transactionTax = transaction.tax,
                reportSize = reportSize,
                reportTax = report.tax
            )

            deleteTransaction = null
            deleteModel
        }
}