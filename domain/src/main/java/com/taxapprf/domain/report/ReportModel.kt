package com.taxapprf.domain.report

data class ReportModel(
    val id: Int,
    val reportKey: String,
    val tax: Double,
    val size: Int,
    val isSync: Boolean,
    val isDelete: Boolean,
    val syncAt: Long,
)