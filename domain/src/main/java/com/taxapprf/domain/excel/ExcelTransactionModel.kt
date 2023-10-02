package com.taxapprf.domain.excel

data class ExcelTransactionModel(
    val name: String?,
    val appDate: String,
    val titleType: String?,
    val sum: Double,
    val currencyCharCode: String,
    val currencyRate: Double?,
    val tax: Double?,
)