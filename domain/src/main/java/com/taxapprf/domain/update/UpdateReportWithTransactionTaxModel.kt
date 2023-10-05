package com.taxapprf.domain.update

data class UpdateReportWithTransactionTaxModel(
    val reportId: Int?,
    val transactionId: Int,

    val currencyOrdinal: Int,

    val date: Long,
    val typeK: Int,
    val sum: Double,
    val oldTax: Double?
) {
    var rate: Double? = null
        set(value) {
            field = value
            calculate()
        }
    var tax: Double? = null

    private fun calculate() {
        rate?.let { rate ->
            tax = (sum * rate * RF_TAX * typeK)
        }
    }

    companion object {
        const val RF_TAX = 0.13
    }
}