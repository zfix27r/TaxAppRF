package com.taxapprf.taxapp.ui.reports

import com.taxapprf.domain.transactions.ReportModel

interface ReportsAdapterCallback {
    fun onItemClick(reportModel: ReportModel)
    fun onMoreClick(reportModel: ReportModel)
}