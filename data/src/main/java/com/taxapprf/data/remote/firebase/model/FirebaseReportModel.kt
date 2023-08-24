package com.taxapprf.data.remote.firebase.model

import com.taxapprf.domain.report.ReportModel

data class FirebaseReportModel(
    val year: String? = null,
    val tax: Double? = null,
    val size: Int? = null
) {
    fun toReportModel(): ReportModel? {
        val year = this.year ?: return null
        val tax = this.tax ?: return null
        val size = this.size ?: return null

        return ReportModel(year, tax, size)
    }
}