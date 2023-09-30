package com.taxapprf.domain.cbr

data class RateWithCurrencyModel(
    val name: String,
    val charCode: String,
    val numCode: Int,
    val rate: Double?,
)