package com.taxapprf.data

import com.taxapprf.domain.FirebaseRequestModel
import com.taxapprf.domain.transaction.GetTransactionModel
import com.taxapprf.domain.transaction.SaveTransactionModel
import com.taxapprf.domain.year.SaveYearSumModel
import com.taxapprf.domain.TransactionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class TransactionRepositoryImpl @Inject constructor(
    private val firebase: FirebaseAPI,
) : TransactionRepository {
    override fun getTransactionModel(requestModel: FirebaseRequestModel) = flow {
        emit(firebase.getTransaction(requestModel))
    }

    override fun getTransactionModels(requestModel: FirebaseRequestModel): Flow<List<GetTransactionModel>> {
        TODO("Not yet implemented")
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