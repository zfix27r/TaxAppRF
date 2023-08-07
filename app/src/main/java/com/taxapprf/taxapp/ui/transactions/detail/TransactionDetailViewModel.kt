package com.taxapprf.taxapp.ui.transactions.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
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
import javax.inject.Inject


@HiltViewModel
class TransactionDetailViewModel @Inject constructor(
    private val saveTransactionUseCase: SaveTransactionUseCase,
) : BaseViewModel() {
    private val _transaction = MutableLiveData<TransactionModel?>()
    val transaction: LiveData<TransactionModel?> = _transaction

    val saveTransaction = SaveTransactionModel()

    fun updateDate(year: Int, month: Int, dayOfMonth: Int): String {
        saveTransaction.updateDate(year, month, dayOfMonth)
        return saveTransaction.date
    }

    fun saveTransaction() = viewModelScope.launch(Dispatchers.IO) {
        saveTransactionUseCase.execute(saveTransaction)
            .onStart { loading() }
            .catch { error(it) }
            .collectLatest { success(BaseState.SuccessEdit) }
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