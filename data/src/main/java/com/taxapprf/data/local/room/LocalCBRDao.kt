package com.taxapprf.data.local.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.taxapprf.data.local.room.entity.LocalCBRCurrencyEntity
import com.taxapprf.data.local.room.entity.LocalCBRRateEntity
import com.taxapprf.data.local.room.model.GetCachedLocalCBRCurrency
import kotlinx.coroutines.flow.Flow

@Dao
interface LocalCBRDao {
    @Query("SELECT id, char_code FROM cbr_currency")
    fun getCachedCurrencies(): List<GetCachedLocalCBRCurrency>

    @Query("SELECT rate FROM cbr_rate WHERE currency_id = :currencyId AND date = :date LIMIT 1")
    fun getCurrencyRate(currencyId: Int, date: Long): Double?

    @Query("SELECT * FROM cbr_currency")
    fun getCurrencies(): Flow<List<LocalCBRCurrencyEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveCurrency(localCBRCurrencyEntity: LocalCBRCurrencyEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveRate(localCBRRateEntity: LocalCBRRateEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveRates(localCBRRateEntities: List<LocalCBRRateEntity>): List<Long>
}