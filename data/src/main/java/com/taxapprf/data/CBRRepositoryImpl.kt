package com.taxapprf.data

import com.taxapprf.data.local.room.LocalCBRDao
import com.taxapprf.data.local.room.entity.LocalCBRCurrencyEntity
import com.taxapprf.data.local.room.entity.LocalCBRRateEntity
import com.taxapprf.data.remote.cbr.RemoteCBRDao
import com.taxapprf.data.remote.cbr.entity.RemoteValCursEntity
import com.taxapprf.data.remote.cbr.entity.RemoteValuteEntity
import com.taxapprf.domain.CBRRepository
import com.taxapprf.domain.NetworkManager
import com.taxapprf.domain.currency.CurrencyWithRateModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class CBRRepositoryImpl @Inject constructor(
    private val networkManager: NetworkManager,
    private val localCBRDao: LocalCBRDao,
    private val remoteCBRDao: RemoteCBRDao,
) : CBRRepository {
    override suspend fun getRate(date: Long, currencyCharCode: String) =
        localCBRDao.getCurrencyRate(currencyCharCode, date)
            ?: run {
                if (networkManager.available) {
                    val formattedDate = date.toCBRDate()
                    val valCurs = remoteCBRDao.getValCurs(formattedDate).execute().body()
                    valCurs?.getCurrencyRate(currencyCharCode, date)
                } else null
            }

    override suspend fun getCurrenciesWithRate(date: Long): List<CurrencyWithRateModel> {
        TODO("Not yet implemented")
    }

    private fun Long.toCBRDate(): String {
        val formatter = DateTimeFormatter.ofPattern(patternCBRDate)
        return LocalDate.ofEpochDay(this).format(formatter)
    }

    private fun RemoteValuteEntity.toLocalCBRRateEntity(
        date: Long,
        charCode: String
    ): LocalCBRRateEntity? {
        try {
            val nominal = nominal ?: return null
            val value = value ?: return null
            val rate = value.formatValueDot().toDouble() / nominal

            return LocalCBRRateEntity(
                charCode = charCode,
                date = date,
                rate = rate
            )
        } catch (_: Exception) {
            return null
        }
    }

    private fun RemoteValCursEntity.getCurrencyRate(
        currencyCharCode: String,
        date: Long
    ): Double? {
        val localCurrencies = localCBRDao.getCurrencies().toMutableSet()
        val localRates = mutableListOf<LocalCBRRateEntity>()
        var rate: Double? = null

        currencies?.forEach { valute ->
            valute.charCode?.let { charCode ->
                if (!localCurrencies.contains(charCode))
                    valute.toLocalCBRCurrencyEntity(charCode)?.let { localCBRDao.saveCurrency(it) }

                valute.toLocalCBRRateEntity(date, charCode)?.let {
                    if (it.charCode == currencyCharCode) rate = it.rate
                    localRates.add(it)
                }
            }
        }

        localCBRDao.saveRates(localRates)

        return rate
    }

    private fun RemoteValuteEntity.toLocalCBRCurrencyEntity(charCode: String): LocalCBRCurrencyEntity? {
        val name = name ?: return null
        val numCode = numCode ?: return null

        return LocalCBRCurrencyEntity(
            charCode = charCode,
            name = name,
            numCode = numCode,
        )
    }

    private fun String.formatValueDot() = replace(',', '.')
}