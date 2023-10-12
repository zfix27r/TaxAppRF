package com.taxapprf.domain.transactions

data class ReportModel(
    val id: Int,
    val name: String,
    val tax: Double,
    val size: Int,
)