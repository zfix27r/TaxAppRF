package com.taxapprf.data.local.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.taxapprf.data.local.room.entity.LocalReportEntity
import com.taxapprf.data.local.room.entity.LocalTransactionEntity

@Dao
interface LocalMainDao {
    @Query("SELECT * FROM report WHERE id = :reportId LIMIT 1")
    fun getLocalReportEntity(reportId: Int): LocalReportEntity?

    @Query("SELECT * FROM report WHERE account_id = :accountId AND remote_key = :remoteKey LIMIT 1")
    fun getLocalReportEntity(accountId: Int, remoteKey: String): LocalReportEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveReport(localReportEntity: LocalReportEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveTransaction(localTransactionEntity: LocalTransactionEntity): Long

    @Delete
    fun deleteReport(localReportEntity: LocalReportEntity): Int
}