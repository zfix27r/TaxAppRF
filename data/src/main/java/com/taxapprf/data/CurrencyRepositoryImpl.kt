package com.taxapprf.data

import com.taxapprf.data.error.DataErrorCBR
import com.taxapprf.data.error.DataErrorConnection
import com.taxapprf.data.remote.cbrapi.CBRAPI
import com.taxapprf.domain.CurrencyRepository
import com.taxapprf.domain.currency.CurrencyModel
import kotlinx.coroutines.flow.flow
import java.lang.Exception
import javax.inject.Inject


class CurrencyRepositoryImpl @Inject constructor(
    private val cbrapi: CBRAPI,
) : CurrencyRepository {
    override fun getTodayCurrency(date: String) = flow {
        try {
            val request = cbrapi.getCurrency(date).execute()

            try {
                emit(
                    request.body()!!.currencyList!!.mapNotNull {
                        CurrencyModel(
                            name = it.name,
                            code = it.charCode,
                            rate = it.value
                        )
                    }
                )
            } catch (_: Exception) {
                throw DataErrorCBR()
            }

        } catch (_: Exception) {
            throw DataErrorConnection()
        }
    }
}