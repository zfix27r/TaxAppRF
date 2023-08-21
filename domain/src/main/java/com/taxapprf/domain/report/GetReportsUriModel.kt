package com.taxapprf.domain.report

import com.taxapprf.domain.transaction.TransactionModel

data class GetReportsUriModel(
    val report: ReportModel,
    val transactions: List<TransactionModel>
)