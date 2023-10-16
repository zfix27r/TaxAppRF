package com.taxapprf.taxapp.ui.currency.converter

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.taxapprf.data.getEpochDate
import com.taxapprf.domain.currency.CurrencyConverterModel
import com.taxapprf.domain.currency.GetCurrencyRateModelsUseCase
import com.taxapprf.taxapp.ui.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class CurrencyConverterViewModel @Inject constructor(
    private val getCurrencyRateModelsUseCase: GetCurrencyRateModelsUseCase,
) : BaseViewModel() {
    val converter = CurrencyConverterModel()

    val sum = MutableLiveData<Double>()
    val sumRub = MutableLiveData<Double>()

    fun loading() = viewModelScope.launch(Dispatchers.IO) {
        converter.currencies = getCurrencyRateModelsUseCase.execute(getEpochDate())

        updateRate()
        setSum(converter.sum)
        success()
    }

    fun setSum(newSum: Double) {
        converter.isModeSum = true
        converter.sum = newSum
        calculate()
        sumRub.postValue(converter.sumRub)
    }

    fun setSumRub(newSum: Double) {
        converter.isModeSum = false
        converter.sumRub = newSum
        calculate()
        sum.postValue(converter.sum)
    }

    var currencyOrdinal: Int
        get() = converter.currencyOrdinal
        set(value) {
            converter.currencyOrdinal = value
            updateRate()
        }

    private fun updateRate() {
        converter.currencyRate =
            converter.currencies.find { it.currency.ordinal == currencyOrdinal }?.rate
                ?: CurrencyConverterModel.DEFAULT_CURRENCY_RATE
    }

    private fun calculate() {
        with(converter) {
            if (isModeSum) sumRub = (sum * currencyRate)
            else sum = (sumRub / currencyRate)
        }
    }
}