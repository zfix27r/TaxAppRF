package com.taxapprf.taxapp.ui.currency.converter

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.taxapprf.domain.cbr.CurrencyConverterModel
import com.taxapprf.domain.cbr.GetCBRRatesUseCase
import com.taxapprf.taxapp.ui.BaseViewModel
import com.taxapprf.taxapp.ui.round
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject


@HiltViewModel
class CurrencyConverterViewModel @Inject constructor(
    private val getTodayCBRRateUseCase: GetCBRRatesUseCase,
) : BaseViewModel() {
    val converter = CurrencyConverterModel()

    val sum = MutableLiveData<Double>()
    val sumRub = MutableLiveData<Double>()

    fun loading() = viewModelScope.launch(Dispatchers.IO) {
        converter.currencies = getTodayCBRRateUseCase.execute(LocalDate.now().toEpochDay())

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

    var currency: String
        get() = converter.currency
        set(value) {
            converter.currency = value
            updateRate()
        }

    private fun updateRate() {
        converter.rateCBR = converter.currencies.find { it.charCode == currency }?.rate
            ?: CurrencyConverterModel.DEFAULT_RATE_CBR
    }

    private fun calculate() {
        with(converter) {
            if (isModeSum) sumRub = (sum * rateCBR).round()
            else sum = (sumRub / rateCBR).round()
        }
    }
}