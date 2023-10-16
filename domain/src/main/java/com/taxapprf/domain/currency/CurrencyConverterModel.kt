package com.taxapprf.domain.currency

class CurrencyConverterModel {
    var sum: Double = DEFAULT_SUM
    var currencyOrdinal: Int = DEFAULT_CURRENCY_ORDINAL
    var currencies: List<CurrencyRateModel> = listOf()
    var currencyRate: Double = DEFAULT_CURRENCY_RATE
    var sumRub: Double = DEFAULT_SUM_RUB
    var isModeSum: Boolean = true

    companion object {
        const val DEFAULT_SUM = 1.0
        const val DEFAULT_CURRENCY_ORDINAL = 14
        const val DEFAULT_CURRENCY_RATE = 0.0
        const val DEFAULT_SUM_RUB = 0.0
    }
}