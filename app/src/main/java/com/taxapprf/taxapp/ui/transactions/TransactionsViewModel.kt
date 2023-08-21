package com.taxapprf.taxapp.ui.transactions

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.taxapprf.domain.report.ReportModel
import com.taxapprf.domain.transaction.DeleteTransactionModel
import com.taxapprf.domain.transaction.DeleteTransactionUseCase
import com.taxapprf.domain.transaction.GetExcelToShareModel
import com.taxapprf.domain.transaction.GetExcelToShareUseCase
import com.taxapprf.domain.transaction.GetExcelToStorageModel
import com.taxapprf.domain.transaction.GetExcelToStorageUseCase
import com.taxapprf.domain.transaction.GetTransactionsModel
import com.taxapprf.domain.transaction.GetTransactionsUseCase
import com.taxapprf.domain.transaction.TransactionModel
import com.taxapprf.taxapp.ui.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class TransactionsViewModel @Inject constructor(
    private val getTransactionsUseCase: GetTransactionsUseCase,
    private val deleteTransactionUseCase: DeleteTransactionUseCase,
    private val getExcelToShareUseCase: GetExcelToShareUseCase,
    private val getExcelToStorageUseCase: GetExcelToStorageUseCase,
) : BaseViewModel() {
    lateinit var report: ReportModel

    private val _transactions = MutableLiveData<List<TransactionModel>>()
    val transactions: LiveData<List<TransactionModel>> = _transactions

    var deleteTransaction: TransactionModel? = null
    lateinit var excelUri: Uri

    fun loadTransactions() = viewModelScope.launch(Dispatchers.IO) {
        val getTransactionsModel = GetTransactionsModel(account.name, report.year)
        getTransactionsUseCase.execute(getTransactionsModel)
            .onStart { start() }
            .catch { error(it) }
            .collectLatest {
                _transactions.postValue(it)
                success()
            }
    }

    fun deleteTransaction() = viewModelScope.launch(Dispatchers.IO) {
        deleteTransaction?.let { transaction ->
            val deleteTransactionModel =
                DeleteTransactionModel(
                    accountKey = account.name,
                    yearKey = report.year,
                    transactionKey = transaction.key,
                    transactionTax = transaction.tax,
                    reportTax = report.tax,
                    reportSize = report.size,
                )

            deleteTransaction = null

            deleteTransactionUseCase.execute(deleteTransactionModel)
                .onStart { start() }
                .catch { error(it) }
                .collectLatest { success() }
        }
    }

    fun saveReportsFromExcel(filePath: String?) = viewModelScope.launch(Dispatchers.IO) {
        filePath?.let { uri ->
/*            val saveReportsFromUriModel = SaveReportsFromUriModel(uri)
            saveReportsFromUriUseCase.execute(saveReportsFromUriModel)
                .onStart { start() }
                .catch { error(it) }
                .collectLatest { success() }*/
        }
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
}