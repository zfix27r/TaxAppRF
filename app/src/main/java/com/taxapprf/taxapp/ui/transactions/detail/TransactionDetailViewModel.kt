package com.taxapprf.taxapp.ui.transactions.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.taxapprf.domain.transaction.SaveTransactionModel
import com.taxapprf.domain.transaction.SaveTransactionUseCase
import com.taxapprf.domain.transaction.TransactionModel
import com.taxapprf.taxapp.ui.BaseState
import com.taxapprf.taxapp.ui.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class TransactionDetailViewModel @Inject constructor(
    private val saveTransactionUseCase: SaveTransactionUseCase,
) : BaseViewModel() {
    private val _transaction = MutableLiveData<TransactionModel?>()
    val transaction: LiveData<TransactionModel?> = _transaction

    val saveTransaction = SaveTransactionModel()

    fun saveTransaction() = viewModelScope.launch(Dispatchers.IO) {
        saveTransactionUseCase.execute(saveTransaction)
            .onStart { loading() }
            .catch { error(it) }
            .collectLatest { success(BaseState.SuccessEdit) }
    }
}