package com.taxapprf.domain.report

data class SaveReportModel(
    val accountKey: String,
    val yearKey: String,

    val name: String,
    val tax: String
)