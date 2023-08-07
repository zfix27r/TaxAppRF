package com.taxapprf.taxapp.ui.taxes

import com.taxapprf.domain.report.ReportModel

interface ReportsAdapterCallback {
    fun onClick(reportModel: ReportModel)
}