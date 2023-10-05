package com.taxapprf.domain.cbr

data class CurrencyRateModel(
    val currency: Currencies,
    val rate: Double?,
) {
    var localCurrencyName: String? = null
}