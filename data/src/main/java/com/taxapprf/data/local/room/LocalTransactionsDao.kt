package com.taxapprf.data.local.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.taxapprf.data.local.room.entity.LocalAccountEntity
import com.taxapprf.data.local.room.entity.LocalDeletedKeyEntity
import com.taxapprf.data.local.room.entity.LocalReportEntity
import com.taxapprf.data.local.room.entity.LocalTransactionEntity

@Dao
interface LocalTransactionsDao {
    @Query(
        "SELECT a.* " +
                "FROM `transaction` t " +
                "JOIN report r ON r.id = t.report_id " +
                "JOIN account a ON a.id = r.account_id " +
                "WHERE t.id = :transactionsId"
    )
    fun getLocalAccountEntity(transactionsId: Int): LocalAccountEntity?

    @Query(
        "SELECT r.* " +
                "FROM `transaction` t " +
                "JOIN report r ON r.id = t.report_id " +
                "WHERE t.id = :transactionsId"
    )
    fun getLocalReportEntity(transactionsId: Int): LocalReportEntity?

    @Query("SELECT * FROM `transaction` WHERE id IN (:transactionsId)")
    fun getLocalTransactionEntities(transactionsId: List<Int>): List<LocalTransactionEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveReport(localReportEntity: LocalReportEntity): Long

    @Delete
    fun deleteReport(localReportEntity: LocalReportEntity): Int

    @Delete
    fun deleteTransactions(localTransactionEntities: List<LocalTransactionEntity>): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveDeletedKeys(localDeletedKeyEntities: List<LocalDeletedKeyEntity>): List<Long>
}