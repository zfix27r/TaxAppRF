package com.taxapprf.data.local.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.taxapprf.data.local.room.entity.LocalDeletedKeyEntity

@Dao
interface LocalDeletedKeyDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveAll(localDeletedKeyEntities: List<LocalDeletedKeyEntity>): List<Long>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun save(localDeletedKeyEntity: LocalDeletedKeyEntity): Long
}