package com.taxapprf.domain.report

data class SaveReportModel(
    val accountKey: String,
    val key: String,
    val tax: Double,
    val size: Int,
)