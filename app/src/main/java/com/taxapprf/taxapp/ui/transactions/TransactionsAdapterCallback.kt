package com.taxapprf.taxapp.ui.transactions

import com.taxapprf.domain.transaction.TransactionModel

interface TransactionsAdapterCallback {
    fun onItemClick(transactionModel: TransactionModel)
    fun onMoreClick(transactionModel: TransactionModel)
    fun onSwiped(transactionModel: TransactionModel)
}