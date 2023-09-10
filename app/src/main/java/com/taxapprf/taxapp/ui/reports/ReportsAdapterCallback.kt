package com.taxapprf.taxapp.ui.reports

import com.taxapprf.domain.report.ReportModel

interface ReportsAdapterCallback {
    fun onClick(reportModel: ReportModel)
    fun onClickMore(reportModel: ReportModel)
    fun onSwiped(reportModel: ReportModel)
}