package com.taxapprf.taxapp.ui.currency.rate

import androidx.lifecycle.viewModelScope
import com.taxapprf.domain.cbr.GetCBRRatesUseCase
import com.taxapprf.domain.cbr.RateWithCurrencyModel
import com.taxapprf.taxapp.ui.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class CurrencyRateViewModel @Inject constructor(
    private val getCBRRatesUseCase: GetCBRRatesUseCase,
) : BaseViewModel() {
    private val now = LocalDate.now().toEpochDay()

    private val _ratesWithCurrency: MutableStateFlow<List<RateWithCurrencyModel>?> = MutableStateFlow(null)
    val ratesWithCurrency = _ratesWithCurrency.asStateFlow()

    var date = now
        set(value) {
            field = if (value.isDateRangeIncorrect()) now else value
        }

    fun updateRatesWithCurrency() = viewModelScope.launch(Dispatchers.IO) {
        _ratesWithCurrency.value = getCBRRatesUseCase.execute(date)
        success()
    }
    private fun Long.isDateRangeIncorrect() =
        this > now
}