package com.taxapprf.taxapp.ui.currency.rate

import androidx.lifecycle.viewModelScope
import com.taxapprf.domain.cbr.CurrencyRateModel
import com.taxapprf.domain.cbr.GetCurrencyRateModelsUseCase
import com.taxapprf.taxapp.ui.BaseViewModel
import com.taxapprf.taxapp.ui.showLoading
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class CurrencyRateViewModel @Inject constructor(
    private val getCurrencyRateModelsUseCase: GetCurrencyRateModelsUseCase,
) : BaseViewModel() {
    private val now
        get() = LocalDate.now().toEpochDay()

    private val _ratesWithCurrency = MutableStateFlow<List<CurrencyRateModel>>(emptyList())
    val ratesWithCurrency = _ratesWithCurrency.asStateFlow()

    var date = now
        set(value) {
            field = if (value.isDateRangeIncorrect()) now else value
        }

    fun updateRatesWithCurrency() =
        viewModelScope.launch(Dispatchers.IO) {
            flow {
                emit(getCurrencyRateModelsUseCase.execute(date))
            }
                .flowOn(Dispatchers.IO)
                .showLoading()
                .collectLatest { _ratesWithCurrency.value = it }
        }

    private fun Long.isDateRangeIncorrect() =
        this > now
}