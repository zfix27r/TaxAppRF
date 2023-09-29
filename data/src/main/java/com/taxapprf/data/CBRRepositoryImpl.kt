package com.taxapprf.data

import com.taxapprf.data.local.room.LocalCBRDao
import com.taxapprf.data.local.room.entity.LocalCBRCurrencyEntity
import com.taxapprf.data.local.room.entity.LocalCBRRateEntity
import com.taxapprf.data.remote.cbr.RemoteCBRDao
import com.taxapprf.data.remote.cbr.entity.RemoteValCursEntity
import com.taxapprf.data.remote.cbr.entity.RemoteValuteEntity
import com.taxapprf.domain.CBRRepository
import com.taxapprf.domain.cbr.CurrencyModel
import com.taxapprf.domain.cbr.CurrencyWithRateModel
import kotlinx.coroutines.flow.map
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
    override suspend fun getCurrencyRate(currencyId: Int, date: Long) =
        localCBRDao.getCurrencyRate(currencyId, date)
            ?: tryGetRemoteRate(currencyId, date)

    override fun getCurrencies() =
        localCBRDao.getCurrencies().map { it.toListCurrencyModel() }

    override suspend fun getCurrenciesWithRate(date: Long): List<CurrencyWithRateModel> {
        TODO("Not yet implemented")
    }

    private fun tryGetRemoteRate(currencyRateId: Int, date: Long) =
        try {
            if (networkManager.available) {
                val formattedDate = date.toCBRDate()
                remoteCBRDao.getValCurs(formattedDate).execute().body()
                    ?.cacheResultAndGetCurrencyRate(currencyRateId, date)
            } else null
        } catch (_: Exception) {
            null
        }

    private fun Long.toCBRDate() =
        LocalDate.ofEpochDay(this).format(formatter_cbr_date)

    private fun RemoteValCursEntity.cacheResultAndGetCurrencyRate(
        currencyRateId: Int,
        date: Long
    ): Double? {
        val localCurrencies = mutableMapOf<String, Int>()
        val localRates = mutableListOf<LocalCBRRateEntity>()
        var rate: Double? = null

        localCBRDao.getCachedCurrencies().forEach { currency ->
            localCurrencies[currency.charCode] = currency.id
        }

        currencies?.forEach { valute ->
            valute.charCode?.let { charCode ->
                if (!localCurrencies.contains(charCode))
                    valute.toLocalCBRCurrencyEntity(charCode)?.let { localCBRDao.saveCurrency(it) }

                localCurrencies[charCode]?.let { cbrCurrencyId ->
                    valute.toLocalCBRRateEntity(date, cbrCurrencyId)?.let {
                        if (cbrCurrencyId == currencyRateId) rate = it.rate
                        localRates.add(it)
                    }
                }
            }
        }

        localCBRDao.saveRates(localRates)

        return rate
    }

    private fun RemoteValuteEntity.toLocalCBRRateEntity(
        date: Long,
        cbrCurrencyId: Int
    ): LocalCBRRateEntity? {
        try {
            val nominal = nominal ?: return null
            val value = value ?: return null
            val rate = value.formatValueDot().toDouble() / nominal

            return getLocalCBRRateEntity(cbrCurrencyId, date, rate)
        } catch (_: Exception) {
            return null
        }
    }

    private fun getLocalCBRRateEntity(cbrCurrencyId: Int, date: Long, rate: Double? = null) =
        LocalCBRRateEntity(
            currencyId = cbrCurrencyId,
            date = date,
            rate = rate
        )

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

    private fun List<LocalCBRCurrencyEntity>.toListCurrencyModel() =
        map { with(it) { CurrencyModel(id, name, charCode, numCode) } }

    companion object {
        private const val PATTERN_CBR_DATE = "dd/MM/uuuu"
        private val formatter_cbr_date = DateTimeFormatter.ofPattern(PATTERN_CBR_DATE)
    }
}