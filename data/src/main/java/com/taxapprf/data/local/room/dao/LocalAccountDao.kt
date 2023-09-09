package com.taxapprf.data.local.room.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.taxapprf.data.local.room.entity.LocalAccountEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LocalAccountDao {
    @Query("SELECT * FROM account WHERE is_delete = 0")
    fun observeAll(): Flow<List<LocalAccountEntity>>

    @Query("SELECT * FROM account")
    fun getAll(): List<LocalAccountEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveAll(localAccountEntity: LocalAccountEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveAll(localAccountEntities: List<LocalAccountEntity>): List<Long>

    @Delete
    fun deleteAll(localAccountEntities: List<LocalAccountEntity>): Int
}