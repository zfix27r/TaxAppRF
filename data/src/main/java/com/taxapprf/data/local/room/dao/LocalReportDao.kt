package com.taxapprf.data.local.room.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.taxapprf.data.local.room.entity.LocalReportEntity
import com.taxapprf.data.local.room.model.LocalDeleteReportModel
import kotlinx.coroutines.flow.Flow

@Dao
interface LocalReportDao {
    @Query("SELECT * FROM report WHERE account_key = :accountKey AND year_key = :yearKey LIMIT 1")
    fun observe(accountKey: String, yearKey: String): Flow<List<LocalReportEntity>>

    @Query("SELECT * FROM report WHERE account_key = :accountKey")
    fun observeAll(accountKey: String): Flow<List<LocalReportEntity>>

    @Query("SELECT * FROM report WHERE account_key = :accountKey AND year_key = :yearKey LIMIT 1")
    fun get(accountKey: String, yearKey: String): List<LocalReportEntity>

    @Query("SELECT * FROM report WHERE account_key = :accountKey")
    fun getAll(accountKey: String): List<LocalReportEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun save(reportEntity: LocalReportEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun save(reportEntities: List<LocalReportEntity>): List<Long>

    @Delete(entity = LocalReportEntity::class)
    fun delete(localDeleteReportModel: LocalDeleteReportModel): Int

    @Delete
    fun delete(reportEntity: List<LocalReportEntity>): Int
}