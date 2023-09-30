package com.taxapprf.taxapp.ui.currency.today

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
class CurrencyRatesViewModel @Inject constructor(
    private val getCBRRatesUseCase: GetCBRRatesUseCase,
) : BaseViewModel() {
    var date = LocalDate.now().toEpochDay()
    fun ratesWithCurrency() =
        flow {
            val result = getCBRRatesUseCase.execute(date)
            emit(result)
        }
            .showLoading()
            .flowOn(Dispatchers.IO)
            .makeHot(viewModelScope)
}