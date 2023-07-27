package com.taxapprf.domain.transaction

data class SaveTransactionModel(
    val account: String,
    var year: String,

    var key: String? = null,
    var id: String = "",
    var type: String = "",
    var date: String = "",
    var currency: String = "",
    var rateCentralBank: Double = 0.0,
    var sum: Double = 0.0,
    var sumRub: Double = 0.0
)