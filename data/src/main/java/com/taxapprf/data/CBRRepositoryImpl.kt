package com.taxapprf.data

import com.taxapprf.data.local.room.LocalCBRDao
import com.taxapprf.data.local.room.entity.LocalCBRCurrencyEntity
import com.taxapprf.data.local.room.entity.LocalCBRRateEntity
import com.taxapprf.data.local.room.model.GetRatesWithCurrency
import com.taxapprf.data.remote.cbr.RemoteCBRDao
import com.taxapprf.data.remote.cbr.entity.RemoteValCursEntity
import com.taxapprf.data.remote.cbr.entity.RemoteValuteEntity
import com.taxapprf.domain.CBRRepository
import com.taxapprf.domain.cbr.CurrencyModel
import com.taxapprf.domain.cbr.RateWithCurrencyModel
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
    override suspend fun getCurrencyRate(currencyRateId: Int, date: Long) =
        localCBRDao.getCurrencyRate(currencyRateId, date)
            ?: tryGetRemote(date)
                ?.getCurrencyRate(currencyRateId)

    override suspend fun getCurrencies() =
        localCBRDao.getCurrencies().toListCurrencyModel()

    override suspend fun getCurrenciesWithRate(date: Long) =
        localCBRDao.getRatesWithCurrency(date).let { ratesWithCurrency ->
            ratesWithCurrency.ifEmpty {
                tryGetRemote(date)
                localCBRDao.getRatesWithCurrency(date)
            }
        }.toListRateWithCurrencyModel()

    private fun tryGetRemote(date: Long) =
        try {
            if (networkManager.available) {
                val formattedDate = date.toCBRDate()
                remoteCBRDao.getValCurs(formattedDate).execute().body()
                    ?.cacheResult(date)
            } else null
        } catch (_: Exception) {
            null
        }

    private fun Long.toCBRDate() =
        LocalDate.ofEpochDay(this).format(formatter_cbr_date)

    private fun RemoteValCursEntity.cacheResult(
        date: Long
    ): List<LocalCBRRateEntity> {
        val localCurrencies = mutableMapOf<String, Int>()
        val localRates = mutableListOf<LocalCBRRateEntity>()

        localCBRDao.getCachedCurrencies().forEach { currency ->
            localCurrencies[currency.charCode] = currency.id
        }

        currencies?.forEach { valute ->
            valute.charCode?.let { charCode ->
                if (!localCurrencies.contains(charCode))
                    valute.toLocalCBRCurrencyEntity(charCode)?.let { localCBRDao.saveCurrency(it) }

                localCurrencies[charCode]?.let { cbrCurrencyId ->
                    valute.toLocalCBRRateEntity(date, cbrCurrencyId)
                        ?.let { localRates.add(it) }
                }
            }
        }

        localCBRDao.saveRates(localRates)

        return localRates
    }

    private fun List<LocalCBRRateEntity>.getCurrencyRate(currencyRateId: Int) =
        find { it.currencyId == currencyRateId }?.rate

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

    private fun List<GetRatesWithCurrency>.toListRateWithCurrencyModel() =
        map { with(it) { RateWithCurrencyModel(name, charCode, numCode, rate) } }

    companion object {
        private const val PATTERN_CBR_DATE = "dd/MM/uuuu"
        private val formatter_cbr_date = DateTimeFormatter.ofPattern(PATTERN_CBR_DATE)
    }
}