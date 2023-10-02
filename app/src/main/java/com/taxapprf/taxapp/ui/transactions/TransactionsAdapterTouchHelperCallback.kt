package com.taxapprf.taxapp.ui.transactions

import com.taxapprf.domain.transaction.TransactionModel

interface TransactionsAdapterTouchHelperCallback {
    fun onSwiped(transactionModel: TransactionModel)
}