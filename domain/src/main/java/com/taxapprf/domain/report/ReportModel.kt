package com.taxapprf.domain.report

data class ReportModel(
    val id: Int,
    val name: String,
    val tax: Double,
    val size: Int,
)