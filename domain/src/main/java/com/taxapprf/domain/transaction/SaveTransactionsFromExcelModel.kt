package com.taxapprf.domain.transaction

data class SaveTransactionsFromExcelModel(
    val accountKey: String,
    val filePath: String,
)