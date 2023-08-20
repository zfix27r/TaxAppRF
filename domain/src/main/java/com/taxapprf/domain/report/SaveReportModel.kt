package com.taxapprf.domain.report

data class SaveReportModel(
    val accountKey: String,
    val yearKey: String,
    val tax: Double,
    val size: Int,
)