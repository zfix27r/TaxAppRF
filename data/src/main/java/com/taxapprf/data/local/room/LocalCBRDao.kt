package com.taxapprf.data.local.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.taxapprf.data.local.room.entity.LocalCBRCurrencyEntity
import com.taxapprf.data.local.room.entity.LocalCBRRateEntity
import com.taxapprf.data.local.room.model.GetCachedLocalCBRCurrency
import com.taxapprf.data.local.room.model.GetRatesWithCurrency

@Dao
interface LocalCBRDao {
    @Query("SELECT id, char_code FROM cbr_currency")
    fun getCachedCurrencies(): List<GetCachedLocalCBRCurrency>

    @Query("SELECT rate FROM cbr_rate WHERE currency_id = :currencyId AND date = :date LIMIT 1")
    fun getCurrencyRate(currencyId: Int, date: Long): Double?

    @Query("SELECT * FROM cbr_currency")
    fun getCurrencies(): List<LocalCBRCurrencyEntity>

    @Query("SELECT " +
            "c.name ${LocalCBRCurrencyEntity.NAME}, " +
            "c.char_code ${LocalCBRCurrencyEntity.CHAR_CODE}, " +
            "c.num_code ${LocalCBRCurrencyEntity.NUM_CODE}, " +
            "r.rate ${LocalCBRRateEntity.RATE}" +
            " FROM cbr_rate r " +
            "LEFT JOIN cbr_currency c ON r.currency_id = c.id" +
            " WHERE date =:date")
    fun getRatesWithCurrency(date: Long): List<GetRatesWithCurrency>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveCurrency(localCBRCurrencyEntity: LocalCBRCurrencyEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveRate(localCBRRateEntity: LocalCBRRateEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveRates(localCBRRateEntities: List<LocalCBRRateEntity>): List<Long>
}