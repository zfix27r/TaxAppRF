package com.taxapprf.data

import com.taxapprf.data.error.InitErrorBundleIsEmpty
import com.taxapprf.domain.FirebaseRequestModel
import com.taxapprf.domain.TransactionRepository
import com.taxapprf.domain.transaction.SaveTransactionModel
import com.taxapprf.domain.transaction.TransactionModel
import com.taxapprf.domain.year.SaveYearSumModel
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class TransactionRepositoryImpl @Inject constructor(
    private val firebase: FirebaseAPI,
) : TransactionRepository {
    override fun getTransactionModel(requestModel: FirebaseRequestModel) = flow {
        emit(firebase.getTransaction(requestModel))
    }

    override fun getTransactionModels(year: String) = flow {
        if (year.isEmpty()) throw InitErrorBundleIsEmpty()
        emit(firebase.getTransactions(year))
    }

    override fun saveTransactionModel(saveTransactionModel: SaveTransactionModel) = flow {
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
}