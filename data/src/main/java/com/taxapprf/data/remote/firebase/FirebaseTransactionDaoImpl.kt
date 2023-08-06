package com.taxapprf.data.remote.firebase

import com.taxapprf.data.error.DataErrorPassKeyIsEmpty
import com.taxapprf.data.remote.firebase.dao.FirebaseTransactionDao
import com.taxapprf.data.remote.firebase.model.FirebaseTransactionModel
import com.taxapprf.data.safeCall
import com.taxapprf.domain.transaction.DeleteTransactionModel
import com.taxapprf.domain.transaction.GetTransactionModel
import com.taxapprf.domain.transaction.GetTransactionsModel
import com.taxapprf.domain.transaction.SaveTransactionModel
import kotlinx.coroutines.tasks.await
import javax.inject.Inject


class FirebaseTransactionDaoImpl @Inject constructor(
    private val fb: FirebaseAPI,
) : FirebaseTransactionDao {
    override suspend fun getTransactions(getTransactionsModel: GetTransactionsModel) =
        safeCall {
            fb.getTransactionsPath(getTransactionsModel)
                .get()
                .await()
                .children
                .mapNotNull { it.getValue(FirebaseTransactionModel::class.java) }
        }

    override suspend fun getTransaction(getTransactionModel: GetTransactionModel) =
        safeCall {
            fb.getTransactionPath(getTransactionModel)
                .get()
                .await()
                .getValue(FirebaseTransactionModel::class.java)
        }

    override suspend fun updateTransaction(saveTransactionModel: SaveTransactionModel) {
        safeCall {
            with(saveTransactionModel) {
                key?.let {
                    fb.getTransactionPath(toGetTransactionModel())
                        .setValue(saveTransactionModel)
                        .await()
                } ?: throw DataErrorPassKeyIsEmpty()
            }
        }
    }

    private fun SaveTransactionModel.toGetTransactionModel() =
        GetTransactionModel(account, year, key!!)

    override suspend fun addTransaction(saveTransactionModel: SaveTransactionModel) {
        safeCall {
            with(saveTransactionModel) {
                val key = fb.getTransactionPath(toGetTransactionModel())
                    .push().key ?: throw DataErrorPassKeyIsEmpty()

                fb.getTransactionPath(toGetTransactionModel())
                    .child(key)
                    .setValue(saveTransactionModel)
                    .await()
            }
        }
    }

    override suspend fun deleteTransaction(deleteTransactionModel: DeleteTransactionModel) {
        safeCall {
            with(deleteTransactionModel) {
                fb.getTransactionPath(toGetTransactionModel())
                    .setValue(null)
                    .await()
            }
        }
    }

    private fun DeleteTransactionModel.toGetTransactionModel() =
        GetTransactionModel(accountKey, reportKey, transactionKey)
}