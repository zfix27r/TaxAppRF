package com.taxapprf.data.local.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.taxapprf.data.local.room.entity.LocalReportEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LocalReportDao {
    @Query("SELECT * FROM report WHERE account_key = :accountKey AND report_key = :reportKey AND is_delete = 0 LIMIT 1")
    fun observe(accountKey: String, reportKey: String): Flow<LocalReportEntity?>

    @Query("SELECT * FROM report WHERE account_key = :accountKey AND is_delete = 0")
    fun observeAll(accountKey: String): Flow<List<LocalReportEntity>>

    @Query("SELECT * FROM report WHERE  account_key = :accountKey AND report_key = :reportKey LIMIT 1")
    fun get(accountKey: String, reportKey: String): LocalReportEntity?

    @Query("SELECT * FROM report WHERE account_key = :accountKey")
    fun getAll(accountKey: String): List<LocalReportEntity>
    @Query("SELECT * FROM report WHERE account_key = :accountKey AND is_delete = 1")
    fun getAllDelete(accountKey: String): List<LocalReportEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun save(reportEntity: LocalReportEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveAll(reportEntities: List<LocalReportEntity>): List<Long>

    @Query("DELETE FROM report WHERE id = :id")
    fun delete(id: Int): Int

    @Delete
    fun deleteAll(reportEntities: List<LocalReportEntity>): Int

    @Query("UPDATE report SET is_delete = 1, is_sync = 0 WHERE account_key = :accountKey AND report_key = :reportKey")
    fun deleteDeferred(accountKey: String, reportKey: String): Int

    @Query("UPDATE report SET is_delete = 1, is_sync = 0 WHERE id = :id")
    fun deleteDeferred(id: Int): Int

    @Query("UPDATE report SET is_delete = 1, is_sync = 0 WHERE id = :ids")
    fun deleteAllDeferred(ids: List<Int>): Int
}