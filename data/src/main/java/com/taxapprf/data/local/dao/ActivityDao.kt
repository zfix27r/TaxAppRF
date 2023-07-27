package com.taxapprf.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.taxapprf.data.local.entity.UserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ActivityDao {
    @Query("SELECT * FROM account WHERE active = 1 LIMIT 1")
    fun getUserModel(): Flow<UserEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun save(accountEntity: UserEntity): Long

    @Query("UPDATE account SET active = 1 WHERE name = :accountName")
    fun save(accountName: String): Int
}