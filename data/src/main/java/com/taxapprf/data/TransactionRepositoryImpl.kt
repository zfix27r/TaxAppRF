package com.taxapprf.data

import com.taxapprf.data.local.dao.TransactionDao
import com.taxapprf.data.local.entity.TransactionEntity
import com.taxapprf.domain.FirebaseRequestModel
import com.taxapprf.domain.TransactionRepository
import com.taxapprf.domain.transaction.SaveTransactionModel
import com.taxapprf.domain.transaction.TransactionModel
import com.taxapprf.domain.year.SaveYearSumModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class TransactionRepositoryImpl @Inject constructor(
    private val firebase: FirebaseAPI,
    private val transactionDao: TransactionDao,
) : TransactionRepository {
    override fun getTransactionModel(transactionKey: String): Flow<TransactionModel> = flow {
        emit(firebase.getTransaction(transactionKey))
    }

    override fun getTransactions(account: String, year: String) =
        transactionDao.getTransactions(account, year)
            .map { it.toListTransactionModel() }

    override fun saveTransactionModel(saveTransactionModel: SaveTransactionModel) = flow {
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

        emit(firebase.saveTransaction(saveTransactionModel))
    }

    override fun deleteTransaction(requestModel: FirebaseRequestModel) = flow {
        emit(firebase.deleteTransaction(requestModel))
    }

    override fun getYearSum(requestModel: FirebaseRequestModel) = flow {
        emit(firebase.getYearSum(requestModel))
    }

    override fun saveYearSum(saveYearSumModel: SaveYearSumModel) = flow {
        emit(firebase.saveYearSum(saveYearSumModel))
    }

    override fun deleteYearSum(requestModel: FirebaseRequestModel) = flow {
        emit(firebase.deleteYearSum(requestModel))
    }

    private fun List<TransactionEntity>.toListTransactionModel() = map {
        with(it) {
            TransactionModel(key, type, id, date, currency, rateCentralBank, sum, sumRub)
        }
    }
}