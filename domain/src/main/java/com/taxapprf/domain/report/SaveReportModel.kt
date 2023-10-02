package com.taxapprf.domain.report

data class SaveReportModel(
    val id: Int,
    val accountKey: String,
    val reportKey: String,
    val tax: Double,
    val size: Int,
)