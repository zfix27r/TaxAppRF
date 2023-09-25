package com.taxapprf.taxapp.ui.currency.converter

import android.icu.util.LocaleData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.taxapprf.domain.currency.CurrencyConverterModel
import com.taxapprf.domain.currency.GetCBRRatesUseCase
import com.taxapprf.taxapp.ui.BaseViewModel
import com.taxapprf.taxapp.ui.format
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.util.Calendar
import java.util.Locale
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
            if (isModeSum) sumRub = (sum * rateCBR).format()
            else sum = (sumRub / rateCBR).format()
        }
    }
}