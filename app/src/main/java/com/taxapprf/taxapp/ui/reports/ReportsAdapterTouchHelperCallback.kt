package com.taxapprf.taxapp.ui.reports

import com.taxapprf.domain.transactions.ReportModel

interface ReportsAdapterTouchHelperCallback {
    fun onSwiped(reportModel: ReportModel)
}