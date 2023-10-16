package com.taxapprf.domain.currency

data class CurrencyRateModel(
    val currency: Currencies,
    val rate: Double?,
)