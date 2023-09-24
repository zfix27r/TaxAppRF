package com.taxapprf.data.local.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.taxapprf.data.local.room.entity.LocalCBRCurrencyEntity
import com.taxapprf.data.local.room.entity.LocalCBRRateEntity

@Dao
interface LocalCBRDao {
    @Query("SELECT char_code FROM cbr_currency")
    fun getCurrencies(): List<String>

    @Query(
        "SELECT r.rate FROM cbr_currency c " +
                "JOIN cbr_rate r ON r.date = :date " +
                "WHERE c.char_code = :currencyCharCode"
    )
    fun getCurrencyRate(currencyCharCode: String, date: Long): Double?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveCurrency(localCBRCurrencyEntity: LocalCBRCurrencyEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveRates(localCBRRateEntities: List<LocalCBRRateEntity>): List<Long>
}