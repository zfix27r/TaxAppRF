package com.taxapprf.taxapp.ui.transactions

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.taxapprf.domain.transaction.GetTransactionsUseCase
import com.taxapprf.domain.transaction.SaveTransactionUseCase
import com.taxapprf.taxapp.ui.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject


@HiltViewModel
class TransactionsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getTransactionsUseCase: GetTransactionsUseCase,
) : BaseViewModel() {
    private val year = savedStateHandle.get<String>(YEAR) ?: ""

    val transactions = getTransactionsUseCase.execute("2023")
        .onStart { loading() }
        .catch { error(it) }
        .onCompletion { success() }
        .asLiveData(viewModelScope.coroutineContext)


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


    fun deleteYear(year: String?) {
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

    companion object {
        const val YEAR = "year"
    }
}