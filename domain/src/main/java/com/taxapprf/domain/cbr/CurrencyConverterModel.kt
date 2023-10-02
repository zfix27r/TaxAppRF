package com.taxapprf.domain.cbr

data class CurrencyConverterModel(
    var sum: Double = DEFAULT_SUM,
    var currency: String = DEFAULT_CURRENCY,
    var currencies: List<RateWithCurrencyModel> = listOf(),
    var rateCBR: Double = DEFAULT_RATE_CBR,
    var sumRub: Double = DEFAULT_SUM_RUB,
    var isModeSum: Boolean = true,
) {
    companion object {
        const val DEFAULT_SUM = 1.0
        const val DEFAULT_CURRENCY = "USD"
        const val DEFAULT_RATE_CBR = 0.0
        const val DEFAULT_SUM_RUB = 0.0
    }
}