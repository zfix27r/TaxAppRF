package com.taxapprf.taxapp.ui.currency.today

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.taxapprf.domain.currency.CurrencyModel
import com.taxapprf.domain.currency.GetCurrencyRateTodayFromCBRUseCase
import com.taxapprf.taxapp.ui.BaseViewModel
import com.taxapprf.taxapp.ui.format
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class CurrencyRatesTodayViewModel @Inject constructor(
    private val getTodayCBRRateUseCase: GetCurrencyRateTodayFromCBRUseCase,
) : BaseViewModel() {
    private val date = Calendar.getInstance().time

    private val _currencies = MutableLiveData<List<CurrencyModel>>()
    val currencies: LiveData<List<CurrencyModel>> = _currencies

    fun loading() = viewModelScope.launch(Dispatchers.IO) {
        getTodayCBRRateUseCase.execute(date.format())
            .onStart { start() }
            .catch { error(it) }
            .collectLatest {
                _currencies.postValue(it)
                success()
            }
    }
}