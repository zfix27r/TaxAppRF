package com.taxapprf.taxapp.ui.transactions

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.taxapprf.domain.account.AccountModel
import com.taxapprf.domain.excel.SendExcelReportUseCase
import com.taxapprf.domain.report.ReportModel
import com.taxapprf.domain.transaction.DeleteTransactionModel
import com.taxapprf.domain.transaction.DeleteTransactionUseCase
import com.taxapprf.domain.transaction.GetTransactionsModel
import com.taxapprf.domain.transaction.GetTransactionsUseCase
import com.taxapprf.domain.transaction.TransactionModel
import com.taxapprf.taxapp.ui.BaseState
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
    private val sendExcelReportUseCase: SendExcelReportUseCase,
) : BaseViewModel() {
    lateinit var account: AccountModel
    lateinit var report: ReportModel

    private val _transactions = MutableLiveData<List<TransactionModel>>()
    val transactions: LiveData<List<TransactionModel>> = _transactions

    var deleteTransactionKey: String? = null
    var emailUri: Uri? = null

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
        deleteTransactionKey?.let { key ->
            transactions.value?.size?.let { size ->
                val deleteTransactionModel =
                    DeleteTransactionModel(
                        accountKey = account.name,
                        yearKey = report.year,
                        transactionKey = key,
                        isLastTransaction = size == 1
                    )
                deleteTransactionKey = null
                deleteTransactionUseCase.execute(deleteTransactionModel)
                    .onStart { start() }
                    .catch { error(it) }
                    .collectLatest { success() }
            }
        }
    }

    fun getExcelReportUri() = viewModelScope.launch(Dispatchers.IO) {
        transactions.value?.let { transactions ->
            sendExcelReportUseCase.execute(report, transactions)
                .onStart { start() }
                .catch { error(it) }
                .collectLatest {
                    emailUri = it
                    success(BaseState.SuccessSendEmail)
                }
        }
    }
}