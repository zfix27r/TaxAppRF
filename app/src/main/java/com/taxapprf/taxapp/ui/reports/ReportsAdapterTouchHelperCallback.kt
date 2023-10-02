package com.taxapprf.taxapp.ui.reports

import com.taxapprf.domain.report.ReportModel

interface ReportsAdapterTouchHelperCallback {
    fun onSwiped(reportModel: ReportModel)
}