package com.taxapprf.data.remote.firebase.model

import com.taxapprf.domain.report.ReportModel

data class FirebaseReportModel(
    val tax: Double? = null,
    val size: Int? = null,
    val syncAt: Long? = null
) {
    fun toReportModel(reportKey: String?): ReportModel? {
        val key = reportKey ?: return null
        val tax = this.tax ?: return null
        val size = this.size ?: return null
        val syncAt = this.syncAt ?: 0L

        return ReportModel(0, key, tax, size, isSync = true, isDelete = false, syncAt = syncAt)
    }
}