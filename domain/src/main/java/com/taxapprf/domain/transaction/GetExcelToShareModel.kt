package com.taxapprf.domain.transaction

import com.taxapprf.domain.report.ReportModel

data class GetExcelToShareModel(
    val report: ReportModel,
    val transactions: List<TransactionModel>
)