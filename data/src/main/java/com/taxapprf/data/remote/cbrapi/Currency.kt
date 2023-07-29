package com.taxapprf.data.remote.cbrapi

import org.simpleframework.xml.ElementList
import org.simpleframework.xml.Root


@Root(name = "ValCurs", strict = false)
data class Currency(
    val rate: Double? = null,
    @ElementList(name = "Valute", inline = true)
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
}