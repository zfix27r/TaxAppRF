package com.taxapprf.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.taxapprf.data.local.entity.TaxEntity
import com.taxapprf.data.local.entity.TransactionEntity
import com.taxapprf.data.local.model.DeleteTaxDataModel
import com.taxapprf.data.local.model.DeleteTransactionDataModel
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {
    @Query("SELECT * FROM `transaction` WHERE account = :account AND year = :year")
    fun getTransactions(account: String, year: String): Flow<List<TransactionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveTransactions(transactionEntities: List<TransactionEntity>)
    @Delete(entity = TransactionEntity::class)
    fun deleteTransactions(deleteTransactionDataModel: DeleteTransactionDataModel)

    @Query("DELETE FROM `transaction`")
    fun dropTransactions()
}