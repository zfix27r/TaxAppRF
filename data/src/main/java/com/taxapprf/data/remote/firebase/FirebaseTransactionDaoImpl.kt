package com.taxapprf.data.remote.firebase

import com.google.firebase.database.DatabaseReference
import com.taxapprf.data.error.AuthError
import com.taxapprf.data.remote.firebase.dao.FirebaseTransactionDao
import com.taxapprf.data.remote.firebase.model.FirebaseTransactionModel
import com.taxapprf.data.safeCall
import com.taxapprf.domain.transaction.DeleteTransactionModel
import com.taxapprf.domain.transaction.SaveTransactionModel
import kotlinx.coroutines.tasks.await
import javax.inject.Inject


class FirebaseTransactionDaoImpl @Inject constructor(
    private val reference: DatabaseReference
) : FirebaseTransactionDao {
    override suspend fun getTransactions(accountKey: String, reportKey: String) =
        safeCall {
            reference
                .child(TRANSACTIONS)
                .child(accountKey)
                .child(reportKey)
                .get()
                .await()
                .children
                .mapNotNull { it.getValue(FirebaseTransactionModel::class.java) }
        }

    suspend fun getTransaction(accountKey: String, reportKey: String, transactionKey: String) =
        safeCall {
            reference
                .child(TRANSACTIONS)
                .child(accountKey)
                .child(reportKey)
                .child(transactionKey)
                .get()
                .await()
                .getValue(FirebaseTransactionModel::class.java)
        }

    override suspend fun updateTransaction(transaction: SaveTransactionModel) {
        safeCall {
            reference
                .child(TRANSACTIONS)
                .child(transaction.account)
                .child(transaction.year)
                .child(transaction.key!!)
                .setValue(transaction)
                .await()
        }
    }

    override suspend fun addTransaction(transaction: SaveTransactionModel) {
        safeCall {
            val tReference = reference
                .child(TRANSACTIONS)
                .child(transaction.account)
                .child(transaction.year)

            val key = tReference.push().key ?: throw AuthError()
            transaction.key = key

            tReference
                .child(key)
                .setValue(transaction)
                .await()
        }
    }

    override suspend fun deleteTransaction(deleteTransactionModel: DeleteTransactionModel) {
        safeCall {
            with(deleteTransactionModel) {
                reference
                    .child(TRANSACTIONS)
                    .child(accountKey)
                    .child(reportKey)
                    .child(transactionKey)
                    .setValue(null)
                    .await()
            }
        }
    }

    companion object {
        const val TRANSACTIONS = "transactions"
    }
}