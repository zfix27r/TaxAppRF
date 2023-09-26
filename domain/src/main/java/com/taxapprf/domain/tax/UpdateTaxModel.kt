package com.taxapprf.domain.tax

data class UpdateTaxModel(
    val reportId: Int?,
    val transactionId: Int,

    val currencyId: Int,

    val date: Long,
    val type: Int,
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
            tax = (sum * rate * RF_TAX * type)
        }
    }

    companion object {
        const val RF_TAX = 0.13
    }
}