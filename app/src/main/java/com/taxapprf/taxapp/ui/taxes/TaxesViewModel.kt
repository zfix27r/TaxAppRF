package com.taxapprf.taxapp.ui.taxes

import android.content.Intent
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.taxapprf.domain.taxes.GetTaxesUseCase
import com.taxapprf.domain.taxes.SaveTaxesFromExcel
import com.taxapprf.domain.taxes.TaxAdapterModel
import com.taxapprf.taxapp.ui.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TaxesViewModel @Inject constructor(
    private val getTaxesUseCase: GetTaxesUseCase,
    private val saveTaxesFromExcel: SaveTaxesFromExcel
) : BaseViewModel() {
    private val _taxes = MutableLiveData<List<TaxAdapterModel>>()
    val taxes: LiveData<List<TaxAdapterModel>> = _taxes

    fun getTaxes(accountName: String) = viewModelScope.launch(Dispatchers.IO) {
        getTaxesUseCase.execute(accountName)
            .onStart { loading() }
            .catch { error(it) }
            .onEach { if (it.isEmpty()) successWithEmpty() }
            .collectLatest {
                success()
                _taxes.postValue(it) }
    }

    fun saveTaxesFromExcel(intent: Intent?) = viewModelScope.launch(Dispatchers.IO) {
        saveTaxesFromExcel.execute(intent!!.data!!.path!!)
            .onStart { loading() }
            .catch { error(it) }
            .collectLatest { success() }
    }
}