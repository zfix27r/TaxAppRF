package com.taxapprf.taxapp.ui.currency.today

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.taxapprf.domain.cbr.CurrencyWithRateModel
import com.taxapprf.domain.cbr.GetCBRRatesUseCase
import com.taxapprf.taxapp.ui.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class CurrencyRatesTodayViewModel @Inject constructor(
    private val getTodayCBRRateUseCase: GetCBRRatesUseCase,
) : BaseViewModel() {
    private val date = Calendar.getInstance().time

    private val _currencies = MutableLiveData<List<CurrencyWithRateModel>>()
    val currencies: LiveData<List<CurrencyWithRateModel>> = _currencies

    fun loading() = viewModelScope.launch(Dispatchers.IO) {
        _currencies.postValue(getTodayCBRRateUseCase.execute(LocalDate.now().toEpochDay()))
    }
}