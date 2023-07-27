package com.taxapprf.data

import com.taxapprf.data.remote.cbrapi.Controller
import com.taxapprf.domain.CentralBankRepository
import kotlinx.coroutines.flow.flow
import java.lang.Exception
import javax.inject.Inject

class CentralBankRepositoryImpl @Inject constructor() : CentralBankRepository {
    override fun getRate(date: String, currency: String) = flow {
        val ctrl = Controller(date)
        val currenciesCall = ctrl.prepareCurrenciesCall()
        currenciesCall.execute().body()?.let {
            emit(it.getCurrencyRate(currency))
        }
        throw Exception("CentralBankRepositoryImpl недоделки")
    }
}