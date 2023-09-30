package com.taxapprf.taxapp.ui.currency.rate

import androidx.lifecycle.viewModelScope
import com.taxapprf.domain.cbr.GetCBRRatesUseCase
import com.taxapprf.taxapp.ui.BaseViewModel
import com.taxapprf.taxapp.ui.makeHot
import com.taxapprf.taxapp.ui.showLoading
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class CurrencyRateViewModel @Inject constructor(
    private val getCBRRatesUseCase: GetCBRRatesUseCase,
) : BaseViewModel() {
    private val now = LocalDate.now().toEpochDay()

    var date = now
        set(value) {
            field = if (value.isDateRangeIncorrect()) now else value
        }

    fun ratesWithCurrency() =
        flow {
            val result = getCBRRatesUseCase.execute(date)
            emit(result)
        }
            .showLoading()
            .flowOn(Dispatchers.IO)
            .makeHot(viewModelScope)

    private fun Long.isDateRangeIncorrect() =
        this > now
}