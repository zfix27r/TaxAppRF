package com.taxapprf.taxapp.ui.transactions.detail

import com.taxapprf.domain.transaction.SaveTransactionModel
import com.taxapprf.taxapp.ui.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class TransactionDetailViewModel @Inject constructor() : BaseViewModel() {
    val saveTransaction = SaveTransactionModel()
}