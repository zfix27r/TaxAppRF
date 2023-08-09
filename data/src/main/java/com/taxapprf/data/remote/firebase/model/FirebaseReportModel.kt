package com.taxapprf.data.remote.firebase.model

import com.taxapprf.data.error.DataErrorResponseEmpty
import com.taxapprf.domain.report.ReportModel


data class FirebaseReportModel(
    val year: String? = null,
    val tax: Double? = null
) {
    fun toReportModel() = ReportModel(
        year = year ?: throw DataErrorResponseEmpty(),
        tax = tax ?: throw DataErrorResponseEmpty()
    )
}