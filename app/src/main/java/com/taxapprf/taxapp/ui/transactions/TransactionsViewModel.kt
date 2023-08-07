package com.taxapprf.taxapp.ui.transactions

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.taxapprf.domain.FirebasePathModel
import com.taxapprf.domain.report.DeleteReportUseCase
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
    private val deleteTaxUseCase: DeleteReportUseCase,
) : BaseViewModel() {
    lateinit var account: String
    lateinit var year: String

    private val _transactions = MutableLiveData<List<TransactionModel>>()
    val transactions: LiveData<List<TransactionModel>> = _transactions

    fun loadTransactions() = viewModelScope.launch(Dispatchers.IO) {
        val getTransactionsModel = FirebasePathModel(account, year)
        getTransactionsUseCase.execute(getTransactionsModel)
            .onStart { loading() }
            .catch { error(it) }
            .collectLatest {
                _transactions.postValue(it)
                success()
            }
    }

    // TODO нет обработки пустого результа, показ какого то сообщения

    /*        firebaseTransactions = FirebaseTransactions(UserLivaData().getFirebaseUser(), account)
            firebaseTransactions.readTransactions(year, object : DataStatus() {
                fun DataIsLoaded(transactionsDB: List<Transaction>) {
                    transactions.setValue(transactionsDB)
                    mtransactions = transactionsDB
                }

                fun DataIsInserted() {}
                fun DataIsUpdated() {}
                fun DataIsDeleted() {}
            })
            firebaseYearSum = FirebaseYearSum(UserLivaData().getFirebaseUser(), account)
            firebaseYearSum.readYearSum(year, object : DataStatus() {
                fun DataIsLoaded(sumTaxesDB: Double) {
                    sumTaxes.setValue(sumTaxesDB)
                    msumTaxes = sumTaxesDB
                }
            })*/


    fun deleteTax(account: String) = viewModelScope.launch(Dispatchers.IO) {
/*        val request = FirebaseRequestModel(account, year)
        deleteTaxUseCase.execute(request)
            .onStart { loading() }
            .catch { error(it) }
            .collectLatest {
                success(BaseState.SuccessDelete)
            }*/
    }

    fun downloadStatement() {}
    /*        year = settings.getString(Settings.YEAR.name, "")
            return try {
                val excelStatement =
                    CreateExcelInDownload(year, msumTaxes, mtransactions)
                excelStatement.create()
            } catch (e: IOException) {
                //e.printStackTrace(); //...
                null
            }*/


    fun createLocalStatement() {
        /*        return try {
                    val excelStatement =
                        CreateExcelInLocal(getApplication(), year, msumTaxes, mtransactions)
                    var file: File? = null
                    file = excelStatement.create()
                    file
                } catch (e: IOException) {
                    //e.printStackTrace();
                    null
                }*/
    }
}