package com.taxapprf.taxapp.ui.transactions.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.taxapprf.domain.transaction.SaveTransactionModel
import com.taxapprf.domain.transaction.TransactionModel
import com.taxapprf.taxapp.ui.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class TransactionDetailViewModel @Inject constructor() : BaseViewModel() {
    private val _transaction = MutableLiveData<TransactionModel?>()
    val transaction: LiveData<TransactionModel?> = _transaction

    val saveTransaction = SaveTransactionModel()
}