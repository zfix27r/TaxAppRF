package com.taxapprf.data

import com.taxapprf.data.local.room.LocalCurrencyDao
import com.taxapprf.data.local.room.entity.LocalCurrencyRateEntity
import com.taxapprf.data.remote.cbr.RemoteCurrencyDao
import com.taxapprf.data.remote.cbr.entity.RemoteValCursEntity
import com.taxapprf.data.remote.cbr.entity.RemoteValuteEntity
import com.taxapprf.domain.CurrencyRepository
import com.taxapprf.domain.currency.Currencies
import com.taxapprf.domain.currency.CurrencyRateModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class CurrencyRepositoryImpl @Inject constructor(
    private val networkManager: NetworkManager,
    private val localCBRDao: LocalCurrencyDao,
    private val remoteCBRDao: RemoteCurrencyDao,
) : CurrencyRepository {
    override suspend fun getCurrencyRate(currencyOrdinal: Int, date: Long) =
        localCBRDao.getCurrencyRate(currencyOrdinal, date)?.rate
            ?: tryGetRemote(date)
                ?.getCurrencyRate(currencyOrdinal)?.rate

    override suspend fun getCurrencyRateModels(date: Long) =
        localCBRDao.getCurrenciesRate(date).let { ratesWithCurrency ->
            ratesWithCurrency.ifEmpty {
                tryGetRemote(date)
                localCBRDao.getCurrenciesRate(date)
            }
        }.map { it.toCurrencyRateModel() }

    private fun tryGetRemote(date: Long) =
        try {
            if (networkManager.isConnection) {
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
    ): List<LocalCurrencyRateEntity> {
        val localRates = mutableListOf<LocalCurrencyRateEntity>()

        currencies?.forEach { valute ->
            valute.charCode?.let { charCode ->
                try {
                    val currencyOrdinal = Currencies.valueOf(charCode).ordinal
                    valute.toLocalCBRRateEntity(currencyOrdinal, date)
                        ?.let { localRates.add(it) }
                } catch (_: Exception) {

                }
            }
        }

        localCBRDao.saveRates(localRates)

        return localRates
    }

    private fun List<LocalCurrencyRateEntity>.getCurrencyRate(currencyOrdinal: Int) =
        find { it.currencyOrdinal == currencyOrdinal }

    private fun RemoteValuteEntity.toLocalCBRRateEntity(
        cbrCurrencyId: Int,
        date: Long,
    ): LocalCurrencyRateEntity? {
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
        LocalCurrencyRateEntity(
            currencyOrdinal = cbrCurrencyId,
            date = date,
            rate = rate
        )

    private fun String.formatValueDot() = replace(',', '.')

    private fun LocalCurrencyRateEntity.toCurrencyRateModel() =
        CurrencyRateModel(
            currency = Currencies.values()[currencyOrdinal],
            rate = rate?.round(ROUND_SIX)
        )

    companion object {
        private const val PATTERN_CBR_DATE = "dd/MM/uuuu"
        private val formatter_cbr_date = DateTimeFormatter.ofPattern(PATTERN_CBR_DATE)
    }
}