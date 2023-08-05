package com.taxapprf.taxapp.ui.transactions.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.taxapprf.domain.transaction.GetTransactionUseCase
import com.taxapprf.domain.transaction.SaveTransactionModel
import com.taxapprf.domain.transaction.SaveTransactionUseCase
import com.taxapprf.domain.transaction.TransactionModel
import com.taxapprf.taxapp.ui.BaseState
import com.taxapprf.taxapp.ui.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject


@HiltViewModel
class TransactionDetailViewModel @Inject constructor(
    private val getTransactionUseCase: GetTransactionUseCase,
    private val saveTransactionUseCase: SaveTransactionUseCase,
) : BaseViewModel() {
    private val _transaction = MutableLiveData<TransactionModel?>()
    val transaction: LiveData<TransactionModel?> = _transaction

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

    fun loadTransaction(account: String, year: String, transactionKey: String?) =
        viewModelScope.launch(Dispatchers.IO) {
            saveTransaction.account = account
            saveTransaction.year = year

            transactionKey?.let {
                getTransactionUseCase
                    .execute(transactionKey)
                    .onStart { loading() }
                    .catch { error(it) }
                    .collectLatest {
                        saveTransaction.updateTransactionModel(it)
                        _transaction.postValue(it)
                        success()
                    }
            } ?: run { _transaction.postValue(null) }
        }

    fun saveDate(year: Int, month: Int, dayOfMonth: Int): String {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.YEAR, year)
        calendar.set(Calendar.MONTH, month)
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.ROOT)
        transactionDate = dateFormat.format(calendar.time)
        saveTransaction.updateYear()
        return transactionDate
    }


    fun saveTransaction(account: String) = viewModelScope.launch(Dispatchers.IO) {
        saveTransaction.account = account

        saveTransactionUseCase.execute(saveTransaction)
            .onStart { loading() }
            .catch { error(it) }
            .collectLatest {
                success(BaseState.SuccessEdit)
                /*
                                saveTransaction.rateCentralBank = it
                                saveTransaction.calculateSumRub()
                                saveTransactionUseCase.execute(saveTransaction)
                                    .onStart { loading() }
                                    .catch { error(it) }
                                    .collectLatest { success(BaseState.Edited) }
                */

            }
    }

    /*    fun deleteTransaction(account: String) = viewModelScope.launch(Dispatchers.IO) {
            saveTransaction.account = account

            val firebaseRequestModel = FirebaseRequestModel(account, saveTransaction.year)
            getYearSumUseCase.execute(firebaseRequestModel)
                .onStart { loading() }
                .catch { error(it) }
                .collectLatest { oldYearSum ->
                    success()
                    val yearSum = oldYearSum.calculateYearSum()
                    if (yearSum == 0.0) {
                        deleteYearSumUseCase.execute(firebaseRequestModel)
                            .onStart { loading() }
                            .catch { error(it) }
                            .collectLatest {
                                success()
                                deleteTransactionFirebase(account)
                            }
                    } else {
                        val saveYearSumModel = SaveYearSumModel(account, saveTransaction.year, yearSum)
                        saveYearSumUseCase.execute(saveYearSumModel)
                            .onStart { loading() }
                            .catch { error(it) }
                            .collectLatest {
                                success()
                                deleteTransactionFirebase(account)
                            }
                    }
                }
        }

        private suspend fun deleteTransactionFirebase(account: String) {
            _transaction.value?.let { transaction ->
                val deleteModel = FirebaseRequestModel(account, saveTransaction.year, transaction.key)
                deleteTransactionUseCase.execute(deleteModel)
                    .onStart { loading() }
                    .catch { error(it) }
                    .collectLatest { success() }
            }
        }

            private fun String.isErrorInputPasswordChecker(): Boolean {
        if (length < 8) error(InputErrorPasswordLength())
        else return false

        return true
    }

        */
}