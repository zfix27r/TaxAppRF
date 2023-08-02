package com.taxapprf.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.taxapprf.data.local.entity.TaxEntity
import com.taxapprf.data.local.entity.TransactionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TaxDao {
    @Query("SELECT * FROM tax WHERE account = :accountName")
    fun getTaxes(accountName: String): Flow<List<TaxEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveTransactions(transactionEntities: List<TransactionEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveTax(taxEntity: TaxEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveTaxes(taxEntities: List<TaxEntity>)

    @Query("DELETE FROM tax")
    fun dropTaxes()

    @Query("DELETE FROM `transaction`")
    fun dropTransactions()
}