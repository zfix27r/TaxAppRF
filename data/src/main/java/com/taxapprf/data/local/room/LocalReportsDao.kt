package com.taxapprf.data.local.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.taxapprf.data.local.room.entity.LocalReportEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LocalReportsDao {
    @Query("SELECT * FROM report WHERE account_id = :accountId")
    fun observeAccountReports(accountId: Int): Flow<List<LocalReportEntity>>

    @Query("SELECT * FROM report WHERE account_id = :accountId AND remote_key = :reportKey LIMIT 1")
    fun getReport(accountId: Int, reportKey: String): LocalReportEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun save(reportEntity: LocalReportEntity): Long

    @Query("DELETE FROM report WHERE id = :reportId")
    fun delete(reportId: Int): Int

    @Query("UPDATE report SET tax = :tax, size = :size WHERE id = :reportId")
    fun updateReport(reportId: Int, tax: Double, size: Int): Int

    /*    @Query("UPDATE report SET tax = :tax, size = :size WHERE id = :reportId")
        fun update(reportId: Int, tax: Double, size: Int): Int*/

    @Query("DELETE FROM report")
    fun deleteAll()
}