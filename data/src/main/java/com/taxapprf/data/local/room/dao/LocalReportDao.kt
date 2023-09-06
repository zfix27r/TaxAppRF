package com.taxapprf.data.local.room.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.taxapprf.data.local.room.entity.LocalReportEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LocalReportDao {
    @Query("SELECT * FROM report WHERE account_key = :accountKey AND report_key = :reportKey LIMIT 1")
    fun observe(accountKey: String, reportKey: String): Flow<LocalReportEntity?>

    @Query("SELECT * FROM report WHERE account_key = :accountKey")
    fun observeAll(accountKey: String): Flow<List<LocalReportEntity>>

    @Query("SELECT * FROM report WHERE account_key = :accountKey AND report_key = :reportKey LIMIT 1")
    fun get(accountKey: String, reportKey: String): LocalReportEntity?

    @Query("SELECT * FROM report WHERE account_key = :accountKey")
    fun getAll(accountKey: String): List<LocalReportEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun save(reportEntity: LocalReportEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveAll(reportEntities: List<LocalReportEntity>): List<Long>

    @Delete
    fun deleteAll(localReportEntities: List<LocalReportEntity>): Int

    @Query("DELETE FROM report WHERE account_key = :accountKey AND report_key = :reportKey")
    fun delete(accountKey: String, reportKey: String): Int

    @Query("UPDATE report SET is_deferred_delete = 1 WHERE account_key = :accountKey AND report_key = :reportKey")
    fun deleteDeferred(accountKey: String, reportKey: String): Int
}