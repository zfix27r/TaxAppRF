package com.taxapprf.data.remote.firebase.model

import com.taxapprf.data.error.external.DataErrorExternalEmpty
import com.taxapprf.domain.report.ReportModel

data class FirebaseReportModel(
    val year: String? = null,
    val tax: Double? = null,
    val size: Int? = null
) {
    fun toReportModel() = ReportModel(
        year = year ?: throw DataErrorExternalEmpty(),
        tax = tax ?: throw DataErrorExternalEmpty(),
        size = size ?: throw DataErrorExternalEmpty(),
    )
}