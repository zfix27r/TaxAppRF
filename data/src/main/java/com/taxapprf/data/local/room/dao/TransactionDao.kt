package com.taxapprf.data.local.room.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.taxapprf.data.local.room.entity.TransactionEntity
import com.taxapprf.data.local.room.model.DeleteTransactionDataModel
import com.taxapprf.data.local.room.model.TaxWithTransactionsDataModel
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {
    @Query(
        "SELECT t2.sum ${TaxWithTransactionsDataModel.TAX_SUM}, " +
                "t1.`key`, t1.type, t1.id, t1.date, t1.currency, t1.rate_central_bank, t1.sum, t1.sum_rub " +
                "FROM `transaction` t1 " +
                "LEFT JOIN tax t2 ON t2.account = :account AND t2.year = :year " +
                "WHERE t1.account = :account AND t1.year = :year"
    )
    fun getTransactions(account: String, year: String): Flow<List<TaxWithTransactionsDataModel>>

    @Query("SELECT * FROM `transaction` WHERE `key` = :transactionKey LIMIT 1")
    fun getTransaction(transactionKey: String): Flow<TransactionEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveTransaction(transactionEntities: TransactionEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveTransactions(transactionEntities: List<TransactionEntity>)

    @Delete(entity = TransactionEntity::class)
    fun deleteTransactions(deleteTransactionDataModel: DeleteTransactionDataModel)

    @Query("DELETE FROM `transaction`")
    fun dropTransactions()
}