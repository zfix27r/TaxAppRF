package com.taxapprf.domain.report

data class SaveReportModel(
    val accountKey: String,
    val reportKey: String,
    val tax: Double,
    val size: Int,
)