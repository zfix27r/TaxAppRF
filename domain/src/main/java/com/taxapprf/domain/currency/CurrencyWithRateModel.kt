package com.taxapprf.domain.currency

data class CurrencyWithRateModel(
    val name: String,
    val numCode: Int,
    val charCode: String,
    val rate: Double,
)