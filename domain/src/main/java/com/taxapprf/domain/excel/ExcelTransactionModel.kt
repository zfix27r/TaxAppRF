package com.taxapprf.domain.excel

data class ExcelTransactionModel(
    val name: String?,
    val appDate: String,
    val typeName: String,
    val sum: Double,
    val currencyCharCode: String,
    val currencyRate: Double?,
    val tax: Double?,
)