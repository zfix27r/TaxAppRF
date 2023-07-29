package com.taxapprf.taxapp.ui.transaction.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.taxapprf.data.error.InputErrorPasswordLength
import com.taxapprf.domain.FirebaseRequestModel
import com.taxapprf.domain.TransactionType
import com.taxapprf.domain.cbr.GetRateCentralBankUseCase
import com.taxapprf.domain.transaction.DeleteTransactionUseCase
import com.taxapprf.domain.transaction.GetTransactionUseCase
import com.taxapprf.domain.transaction.SaveTransactionModel
import com.taxapprf.domain.transaction.SaveTransactionUseCase
import com.taxapprf.domain.transaction.TransactionModel
import com.taxapprf.domain.year.DeleteYearSumUseCase
import com.taxapprf.domain.year.GetYearSumUseCase
import com.taxapprf.domain.year.SaveYearSumModel
import com.taxapprf.domain.year.SaveYearSumUseCase
import com.taxapprf.taxapp.ui.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.math.RoundingMode
import javax.inject.Inject
import kotlin.math.abs


@HiltViewModel
class TransactionDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getTransactionUseCase: GetTransactionUseCase,
    private val getRateCentralBank: GetRateCentralBankUseCase,
    private val saveTransactionUseCase: SaveTransactionUseCase,
    private val deleteTransactionUseCase: DeleteTransactionUseCase,
    private val getYearSumUseCase: GetYearSumUseCase,
    private val saveYearSumUseCase: SaveYearSumUseCase,
    private val deleteYearSumUseCase: DeleteYearSumUseCase,
) : BaseViewModel() {
    private val account = savedStateHandle.get<String>(ACCOUNT)!!
    private val year = savedStateHandle.get<String>(YEAR)!!
    private val transactionKey = savedStateHandle.get<String>(TRANSACTION_KEY)!!

    private val saveTransaction = SaveTransactionModel(account, year)
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

    val transactionLiveData = getTransactionUseCase
        .execute(transactionKey)
        .onEach { saveTransaction.updateTransactionModel(it) }
        .asLiveData(viewModelScope.coroutineContext)


    private val transactionLiveDataVal
        get() = transactionLiveData.value

    private fun SaveTransactionModel.updateTransactionModel(getTransactionModel: TransactionModel) {
        id = getTransactionModel.id
        key = getTransactionModel.key
        date = getTransactionModel.date
        currency = getTransactionModel.currency
        rateCentralBank = getTransactionModel.rateCentralBank
        sum = getTransactionModel.sum
        sumRub = getTransactionModel.sumRub
    }

    fun saveDate(year: Int, month: Int, dayOfMonth: Int) {

    }

    fun deleteTransaction() =
        viewModelScope.launch(Dispatchers.IO) {
            val firebaseRequestModel = FirebaseRequestModel(account, year)
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
                                deleteTransactionFirebase()
                            }
                    } else {
                        val saveYearSumModel = SaveYearSumModel(account, year, yearSum)
                        saveYearSumUseCase.execute(saveYearSumModel)
                            .onStart { loading() }
                            .catch { error(it) }
                            .collectLatest {
                                success()
                                deleteTransactionFirebase()
                            }
                    }
                }
        }

    private suspend fun deleteTransactionFirebase() {
        val deleteModel = FirebaseRequestModel(account, year, transactionLiveDataVal!!.id)
        deleteTransactionUseCase.execute(deleteModel)
            .onStart { loading() }
            .catch { error(it) }
            .collectLatest { success() }
    }

    fun saveTransaction() = viewModelScope.launch(Dispatchers.IO) {
/*        transactionLiveDataVal?.let {
            if (year != saveTransaction.year) deleteTransactionFirebase()
        }
        getRateCentralBank.execute(saveTransaction.date, saveTransaction.currency)
            .onStart { loading() }
            .catch { error(it) }
            .collectLatest {
                saveTransaction.rateCentralBank = it
                saveTransaction.calculateSumRub()
                saveTransactionUseCase.execute(saveTransaction)
                    .onStart { loading() }
                    .catch { error(it) }
                    .collectLatest { success(BaseState.Edited) }
            }*/
    }

    private fun Double.calculateYearSum(): Double {
        var big = BigDecimal(this - transactionLiveDataVal!!.sumRub)
        big = big.setScale(2, RoundingMode.HALF_UP)
        return big.toDouble()
    }

    private fun SaveTransactionModel.calculateSumRub() {
        val k = when (TransactionType.valueOf(type)) {
            TransactionType.TRADE -> 1
            TransactionType.FUNDING_WITHDRAWAL -> 0
            TransactionType.COMMISSION -> {
                sum = abs(sum)
                -1
            }
        }

        var sumRubBigDecimal = BigDecimal(sum * rateCentralBank * 0.13 * k)
        sumRubBigDecimal = sumRubBigDecimal.setScale(2, RoundingMode.HALF_UP)
        sumRub = sumRubBigDecimal.toDouble()
    }

    private fun String.isErrorInputPasswordChecker(): Boolean {
        if (length < 8) error(InputErrorPasswordLength())
        else return false

        return true
    }

    companion object {
        const val YEAR = "year"
        const val ACCOUNT = "account"
        const val TRANSACTION_KEY = "transaction_key"
    }
}