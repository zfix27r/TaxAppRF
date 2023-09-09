package com.taxapprf.data.local.room.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.taxapprf.data.local.room.entity.LocalReportEntity
import com.taxapprf.data.local.room.entity.LocalTransactionEntity
import com.taxapprf.data.local.room.model.LocalDeleteTransactionModel
import kotlinx.coroutines.flow.Flow

@Dao
interface LocalTransactionDao {
    @Query("SELECT * FROM `transaction` WHERE transaction_key = :transactionKey AND is_delete = 0 LIMIT 1")
    fun observe(transactionKey: String): Flow<LocalTransactionEntity>

    @Query("SELECT * FROM `transaction` WHERE account_key = :accountKey AND report_key = :reportKey AND is_delete = 0")
    fun observeAll(accountKey: String, reportKey: String): Flow<List<LocalTransactionEntity>>

    @Query("SELECT * FROM `transaction` WHERE account_key = :accountKey AND report_key = :reportKey")
    fun getAll(accountKey: String, reportKey: String): List<LocalTransactionEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun save(localTransactionEntity: LocalTransactionEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun save(localTransactionEntities: List<LocalTransactionEntity>): List<Long>
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveAll(transactionEntities: List<LocalTransactionEntity>): List<Long>

    @Delete(entity = LocalTransactionEntity::class)
    fun delete(localDeleteTransactionModel: LocalDeleteTransactionModel): Int

    @Delete
    fun delete(localTransactionEntities: List<LocalTransactionEntity>): Int
    @Delete
    fun deleteAll(transactionEntities: List<LocalTransactionEntity>): Int

    @Query("UPDATE `transaction` SET is_delete = 1, is_sync = 0 WHERE transaction_key = :transactionKey")
    fun deleteDeferred(transactionKey: String): Int

    @Query("UPDATE `transaction` SET is_delete = 1, is_sync = 0 WHERE  account_key = :accountKey AND report_key = :reportKey")
    fun deleteAllDeferred(accountKey: String, reportKey: String): Int
}