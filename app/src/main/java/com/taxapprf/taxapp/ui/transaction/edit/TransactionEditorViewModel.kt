package com.taxapprf.taxapp.ui.transaction.edit

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.taxapprf.domain.transaction.DeleteTransactionUseCase
import com.taxapprf.domain.transaction.GetTransactionUseCase
import com.taxapprf.domain.transaction.SaveTransactionModel
import com.taxapprf.domain.transaction.SaveTransactionUseCase
import com.taxapprf.domain.transaction.TransactionModel
import com.taxapprf.domain.year.DeleteYearSumUseCase
import com.taxapprf.domain.year.GetYearSumUseCase
import com.taxapprf.domain.year.SaveYearSumUseCase
import com.taxapprf.taxapp.ui.BaseState
import com.taxapprf.taxapp.ui.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject


@HiltViewModel
class TransactionEditorViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getTransactionUseCase: GetTransactionUseCase,
    private val saveTransactionUseCase: SaveTransactionUseCase,
    private val deleteTransactionUseCase: DeleteTransactionUseCase,
    private val getYearSumUseCase: GetYearSumUseCase,
    private val saveYearSumUseCase: SaveYearSumUseCase,
    private val deleteYearSumUseCase: DeleteYearSumUseCase,
) : BaseViewModel() {
    private val accountKey = savedStateHandle.get<String>(ACCOUNT_KEY)
    private val transactionKey = savedStateHandle.get<String>(TRANSACTION_KEY)

    val transaction = transactionKey?.let {
        getTransactionUseCase
            .execute(transactionKey)
            .onEach { saveTransaction.updateTransactionModel(it) }
            .asLiveData(viewModelScope.coroutineContext)
    }

    private val saveTransaction = SaveTransactionModel()
    var transactionType
        get() = saveTransaction.type
        set(value) {
            saveTransaction.type = value
        }
    var transactionCurrency
        get() = saveTransaction.currency
        set(value) {
            saveTransaction.currency = value
        }
    var transactionSum
        get() = saveTransaction.sum
        set(value) {
            saveTransaction.sum = value
        }
    var transactionId
        get() = saveTransaction.id
        set(value) {
            saveTransaction.id = value
        }
    var transactionDate
        get() = saveTransaction.date
        set(value) {
            saveTransaction.date = value
        }

    fun saveDate(year: Int, month: Int, dayOfMonth: Int): String {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.YEAR, year)
        calendar.set(Calendar.MONTH, month)
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.ROOT)
        transactionDate = dateFormat.format(calendar.time)
        saveTransaction.year = year.toString()
        return transactionDate
    }

    private fun SaveTransactionModel.updateTransactionModel(getTransactionModel: TransactionModel) {
        id = getTransactionModel.id
        key = getTransactionModel.key
        date = getTransactionModel.date
        currency = getTransactionModel.currency
        rateCentralBank = getTransactionModel.rateCentralBank
        sum = getTransactionModel.sum
        sumRub = getTransactionModel.sumRub
    }

    fun saveTransaction(accountName: String) = viewModelScope.launch(Dispatchers.IO) {
        saveTransaction.accountName = accountName

        saveTransactionUseCase.execute(saveTransaction)
            .onStart { loading() }
            .catch { error(it) }
            .collectLatest { success(BaseState.SuccessEdit) }
    }

    companion object {
        const val ACCOUNT_KEY = "account_key"
        const val TRANSACTION_KEY = "transaction_key"
    }
}