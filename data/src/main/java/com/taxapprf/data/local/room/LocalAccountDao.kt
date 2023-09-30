package com.taxapprf.data.local.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.taxapprf.data.local.room.entity.LocalAccountEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LocalAccountDao {
    @Query("SELECT * FROM account")
    fun observeAll(): Flow<List<LocalAccountEntity>>

    @Query("SELECT * FROM account")
    fun getAll(): List<LocalAccountEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveAll(localAccountEntities: List<LocalAccountEntity>): List<Long>

    @Delete
    fun deleteAll(localAccountEntities: List<LocalAccountEntity>): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun save(localAccountEntity: LocalAccountEntity): Long

    @Query("UPDATE account SET is_active = 0 WHERE is_active = 1")
    fun resetActiveAccount(): Int

    @Query("UPDATE account SET is_active = 1 WHERE remote_key = :accountName")
    fun setActiveAccount(accountName: String): Int
}