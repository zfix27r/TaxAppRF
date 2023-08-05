package com.taxapprf.domain.transaction

data class TransactionModel(
    val key: String,
    val type: String,
    val id: String,
    val date: String,
    val currency: String,
    val rateCentralBank: Double,
    val sum: Double,
    val sumRub: Double
) {


/*    override fun toString(): String {
        return getType() + getId() + getSum().toString() + getCurrency() + getRateCentralBank().toString() + getSumRub().toString()
    }*/

/*    override operator fun compareTo(o: TransactionModel): Int {
        val formatter = SimpleDateFormat("dd/MM/yyyy")
        val date1Str: String = this.date
        val date2Str = o.date
        try {
            val date1 = formatter.parse(date1Str)
            val date2 = formatter.parse(date2Str)
            return if (date1.before(date2)) {
                1
            } else if (date1.after(date2)) {
                -1
            } else 0
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return 0
    }*/
}