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
    @Query("SELECT * FROM report WHERE account_key = :accountKey AND year_key = :yearKey LIMIT 1")
    fun observe(accountKey: String, yearKey: String): Flow<LocalReportEntity>

    @Query("SELECT * FROM report WHERE account_key = :accountKey")
    fun observeAll(accountKey: String): Flow<List<LocalReportEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun save(reportEntity: LocalReportEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun save(reportEntities: List<LocalReportEntity>): List<Long>

    @Delete
    fun delete(reportEntity: LocalReportEntity): Int

    @Delete
    fun delete(reportEntity: List<LocalReportEntity>): Int
}