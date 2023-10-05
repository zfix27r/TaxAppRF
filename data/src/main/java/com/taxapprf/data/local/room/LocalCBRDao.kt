package com.taxapprf.data.local.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.taxapprf.data.local.room.entity.LocalCBRRateEntity

@Dao
interface LocalCBRDao {
    @Query("SELECT * FROM cbr_rate WHERE currency_ordinal = :currencyOrdinal AND date = :date LIMIT 1")
    fun getCurrencyRate(currencyOrdinal: Int, date: Long): LocalCBRRateEntity?

    @Query("SELECT * FROM cbr_rate WHERE date =:date")
    fun getCurrenciesRate(date: Long): List<LocalCBRRateEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveRates(localCBRRateEntities: List<LocalCBRRateEntity>): List<Long>
}