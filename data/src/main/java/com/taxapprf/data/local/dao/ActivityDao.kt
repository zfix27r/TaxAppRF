package com.taxapprf.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import com.taxapprf.data.local.entity.UserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ActivityDao {
    @Query("SELECT * FROM user WHERE active = 1 LIMIT 1")
    fun getUserModel(): Flow<UserEntity?>
}