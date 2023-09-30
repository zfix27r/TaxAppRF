package com.taxapprf.taxapp.ui.reports

import com.taxapprf.domain.report.ReportModel

interface ReportsAdapterCallback {
    fun onItemClick(reportModel: ReportModel)
    fun onMoreClick(reportModel: ReportModel)
}