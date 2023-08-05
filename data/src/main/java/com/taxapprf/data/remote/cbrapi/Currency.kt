/*
package com.taxapprf.data.remote.cbrapi

import com.google.gson.annotations.SerializedName


data class Currency(
    @SerializedName("rate")
    val rate: Double? = null,
    @SerializedName("Valute")
    val currencies: List<CurrencyRate>? = null
) {
    fun getCurrencyRate(valutaCode: String): Double? {
        currencies?.let {
            for (currency in it) {
                if (currency.charCode == valutaCode) {
                    // TODO нет обработок нулевых состояний в ответе
                    return currency.getValue() / currency.nominal!!
                }
            }

        }
        return rate
    }
}*/
