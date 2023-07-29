package com.taxapprf.taxapp.ui.taxes

import android.content.Intent
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.taxapprf.domain.taxes.GetTaxesUseCase
import com.taxapprf.domain.taxes.SaveTaxesFromExcel
import com.taxapprf.domain.transaction.SaveTransactionUseCase
import com.taxapprf.taxapp.ui.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class TaxesViewModel @Inject constructor(
    private val getYearsUseCase: GetTaxesUseCase,
    private val saveTransactionUseCase: SaveTransactionUseCase,
    private val saveTaxesFromExcel: SaveTaxesFromExcel
) : BaseViewModel() {

    val taxes = getYearsUseCase.execute()
        .onStart { loading() }
        .catch { error(it) }
        .onCompletion { success() }
        .asLiveData(viewModelScope.coroutineContext)

    fun saveTaxesFromExcel(intent: Intent?) = viewModelScope.launch(Dispatchers.IO) {
        saveTaxesFromExcel.execute(intent!!.data!!.path!!)
            .onStart { loading() }
            .catch { error(it) }
            .collectLatest { success() }
    }
}