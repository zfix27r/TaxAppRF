package com.taxapprf.data.local.room.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.taxapprf.data.local.room.entity.LocalTransactionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LocalTransactionDao {
    @Query("SELECT * FROM `transaction` WHERE transaction_key = :transactionKey LIMIT 1")
    fun observe(transactionKey: String): Flow<LocalTransactionEntity>

    @Query("SELECT * FROM `transaction` WHERE account_key = :accountKey AND year_key = :yearKey")
    fun observeAll(accountKey: String, yearKey: String): Flow<List<LocalTransactionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun save(localTransactionEntity: LocalTransactionEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun save(localTransactionEntities: List<LocalTransactionEntity>): List<Long>

    @Delete
    fun delete(localTransactionEntity: LocalTransactionEntity): Int

    @Delete
    fun delete(localTransactionEntities: List<LocalTransactionEntity>): Int
}