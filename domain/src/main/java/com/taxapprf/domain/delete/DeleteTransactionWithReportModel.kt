package com.taxapprf.domain.delete

data class DeleteTransactionWithReportModel(
    val transactionId: Int,
) {
    var reportId: Int? = null
    var reportKey: String? = null

    var transactionKey: String? = null
    var transactionTax: Double? = null
}