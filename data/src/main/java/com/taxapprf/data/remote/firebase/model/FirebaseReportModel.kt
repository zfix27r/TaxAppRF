package com.taxapprf.data.remote.firebase.model

import com.taxapprf.domain.report.ReportModel

data class FirebaseReportModel(
    val key: String? = null,
    val tax: Double? = null,
    val size: Int? = null,
    val syncAt: Long? = null
) {
    fun toReportModel(): ReportModel? {
        val key = this.key ?: return null
        val tax = this.tax ?: return null
        val size = this.size ?: return null
        val syncAt = this.syncAt ?: 0L

        return ReportModel(key, tax, size, isSync = true, isDeferredDelete = false, syncAt = syncAt)
    }
}