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
    @Query("SELECT * FROM report WHERE id = :reportId LIMIT 1")
    fun observe(reportId: Int): Flow<LocalReportEntity?>

    @Query("SELECT * FROM report WHERE account_id = :accountId")
    fun observeAll(accountId: Int): Flow<List<LocalReportEntity>>

    @Query("SELECT * FROM report WHERE id = :reportId LIMIT 1")
    fun get(reportId: Int): LocalReportEntity?

    @Query("SELECT * FROM report WHERE account_id = :accountId AND remote_key = :reportKey LIMIT 1")
    fun get(accountId: Int, reportKey: String): LocalReportEntity?

    @Query("SELECT * FROM report WHERE id = :accountId")
    fun getAll(accountId: Int): List<LocalReportEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun save(reportEntity: LocalReportEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveAll(reportEntities: List<LocalReportEntity>): List<Long>

    @Query("DELETE FROM report WHERE id = :reportId")
    fun delete(reportId: Int): Int

    @Delete
    fun deleteAll(reportEntities: List<LocalReportEntity>): Int

    @Query("UPDATE report SET tax = :tax WHERE id = :reportId")
    fun updateTax(reportId: Int, tax: Double): Int

    @Query("UPDATE report SET tax = :tax, size = :size WHERE id = :reportId")
    fun update(reportId: Int, tax: Double, size: Int): Int
}