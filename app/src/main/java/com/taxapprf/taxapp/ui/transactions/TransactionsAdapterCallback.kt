package com.taxapprf.taxapp.ui.transactions

import com.taxapprf.domain.transactions.TransactionModel

interface TransactionsAdapterCallback {
    fun onItemClick(transactionModel: TransactionModel)
    fun onMoreClick(transactionModel: TransactionModel)
}