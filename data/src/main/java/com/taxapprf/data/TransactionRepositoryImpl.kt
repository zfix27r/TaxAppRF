package com.taxapprf.data

import com.taxapprf.data.local.room.dao.TransactionDao
import com.taxapprf.data.local.room.entity.TransactionEntity
import com.taxapprf.data.local.room.model.TaxWithTransactionsDataModel
import com.taxapprf.data.remote.firebase.FirebaseAPI
import com.taxapprf.domain.FirebaseRequestModel
import com.taxapprf.domain.TransactionRepository
import com.taxapprf.domain.transaction.SaveTransactionModel
import com.taxapprf.domain.transaction.TransactionModel
import com.taxapprf.domain.transaction.TransactionsModel
import com.taxapprf.domain.year.SaveYearSumModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class TransactionRepositoryImpl @Inject constructor(
    private val firebase: FirebaseAPI,
    private val transactionDao: TransactionDao,
) : TransactionRepository {
    override fun getTransaction(request: FirebaseRequestModel) = flow {
        emit(firebase.getTransaction(request))
    }

    override fun getTransactions(request: FirebaseRequestModel) =
        transactionDao.getTransactions(request.account, request.year)
            .map { it.toTransactionsModel() }

    override fun saveTransactionModel(
        request: FirebaseRequestModel,
        transaction: SaveTransactionModel
    ): Flow<Unit> = flow {
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
        transaction.key
            ?.let { firebase.updateTransaction(request, transaction) }
            ?: run {
                val key = firebase.addTransaction(request, transaction)
                transaction.key = key
                //transaction.updateFirebaseYear()
            }

        transactionDao.saveTransaction(transaction.toTransactionEntity())
        emit(Unit)
    }

    override fun deleteTransaction(requestModel: FirebaseRequestModel) = flow {
        emit(firebase.deleteTransaction(requestModel))
    }

    override fun getYearSum(requestModel: FirebaseRequestModel) = flow {
        emit(firebase.getYearSum(requestModel))
    }

    override fun saveYearSum(saveYearSumModel: SaveYearSumModel) = flow<Unit> {
//        emit(firebase.saveYearSum(saveYearSumModel))
    }

    override fun deleteYearSum(requestModel: FirebaseRequestModel) = flow {
        emit(firebase.deleteYearSum(requestModel))
    }

    private fun List<TaxWithTransactionsDataModel>.toTransactionsModel() =
        if (isNotEmpty()) {
            TransactionsModel(
                taxSum = filterNotNull().first().taxSum.toString(),
                transactions = map {
                    with(it) {
                        TransactionModel(
                            key, type, id, date, currency,
                            rateCentralBank, sum, sumRub
                        )
                    }
                }
            )
        } else null

    private fun SaveTransactionModel.toTransactionEntity() =
        TransactionEntity(
            key!!, account, year, type, id, date, currency,
            rateCentralBank, sum, sumRub
        )
}