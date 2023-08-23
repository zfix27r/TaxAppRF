package com.taxapprf.data

import com.taxapprf.data.error.cbr.DataErrorCBREmpty
import com.taxapprf.data.remote.cbrapi.CBRAPI
import com.taxapprf.domain.CurrencyRepository
import com.taxapprf.domain.currency.CurrencyModel
import kotlinx.coroutines.flow.flow
import javax.inject.Inject


class CurrencyRepositoryImpl @Inject constructor(
    private val cbrapi: CBRAPI,
) : CurrencyRepository {
    override fun getTodayCurrency(date: String) = flow<List<CurrencyModel>> {
        cbrapi.getCurrency(date).execute().body()?.let { body ->
            emit(
                body.currencyList?.mapNotNull {
                    CurrencyModel(
                        name = it.name,
                        code = it.charCode,
                        rate = it.value
                    )
                } ?: throw DataErrorCBREmpty()
            )
        } ?: throw DataErrorCBREmpty()
    }
}