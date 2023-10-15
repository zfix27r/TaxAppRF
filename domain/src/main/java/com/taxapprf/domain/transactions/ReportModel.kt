package com.taxapprf.domain.transactions

data class ReportModel(
    val id: Int,
    val name: String,
    val sumRUB: Double,
    val taxRUB: Double,
    val size: Int,
)