package com.taxapprf.taxapp.ui.transactions

import com.taxapprf.domain.transaction.TransactionModel

interface TransactionsAdapterCallback {
    fun onClick(transactionModel: TransactionModel)
    fun onClickMore(transactionModel: TransactionModel)
    fun onSwiped(position: Int)
}