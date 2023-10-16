package com.taxapprf.taxapp.ui.transactions

import com.taxapprf.domain.transactions.TransactionModel

interface TransactionsAdapterTouchHelperCallback {
    fun onSwiped(transactionModel: TransactionModel)
}