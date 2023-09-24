package com.taxapprf.data.local.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.taxapprf.data.local.room.entity.LocalCBRCurrencyEntity
import com.taxapprf.data.local.room.entity.LocalCBRRateEntity

@Dao
interface LocalCBRDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveAll(localCBRCurrencyEntities: List<LocalCBRCurrencyEntity>): List<Long>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveAll(localCBRRateEntities: List<LocalCBRRateEntity>): List<Long>
}