package com.taxapprf.taxapp.ui.converter;

import static java.math.BigDecimal.ROUND_HALF_UP;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import com.taxapprf.taxapp.retrofit2.Currencies;
import com.taxapprf.taxapp.ui.newtransaction.DoubleCheck;
import com.taxapprf.taxapp.ui.rates.today.RatesTodayViewModel;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class ConverterViewModel extends ViewModel {
    private Currencies rates;
    private Double rate;
    private final MutableLiveData<String> value;
    private final MutableLiveData<String> rub;
    private String oldValue;
    private String oldRub;
    private String currentCurrency;

    public ConverterViewModel() {
        oldValue = "1";
        currentCurrency = "USD";
        value = new MutableLiveData<>();
        value.setValue(oldValue);
        rub = new MutableLiveData<>();
        rub.setValue("0");
//        RatesTodayViewModel viewModelRates = new ViewModelProvider((ViewModelStoreOwner) owner).get(RatesTodayViewModel.class);
//        viewModelRates.getCurrencies().observe(owner, new Observer<Currencies>() {
//            @Override
//            public void onChanged(Currencies currencies) {
//                rates = currencies;
//                rate = rates.getCurrencyRate(currentCurrency);
//                oldRub = rate.toString();
//                rub.postValue(oldRub);
//            }
//        });
    }

    public void calculateRub (String valueStr) {
        if (rates == null) {
            value.postValue(oldValue);
            return;
        }
        DoubleCheck doubleCheck = new DoubleCheck(valueStr);
        if (!doubleCheck.isCheck()){
            value.postValue(oldValue);
            return;
        }
        oldValue = valueStr.replaceAll(",", "\\.");
        value.postValue(oldValue);
        rate = rates.getCurrencyRate(currentCurrency);
        BigDecimal sum = BigDecimal.valueOf(doubleCheck.getNumDouble())
                .multiply(BigDecimal.valueOf(rate));
        sum.setScale(4, ROUND_HALF_UP);
        String sumStr = sum.toString().replaceAll(",", "\\.");
        rub.postValue(sumStr);
        oldRub = sumStr;
    }

    public void calculateValue (String rubStr) {
        if (rates == null) {
            rub.postValue(oldRub);
            return;
        }
        DoubleCheck doubleCheck = new DoubleCheck(rubStr);
        if (!doubleCheck.isCheck()){
            rub.postValue(oldRub);
            return;
        }
        oldRub = rubStr.replaceAll(",", "\\.");
        rub.postValue(oldRub);
        rate = rates.getCurrencyRate(currentCurrency);
        BigDecimal sum = BigDecimal.valueOf(doubleCheck.getNumDouble()).divide(BigDecimal.valueOf(rate), 4, RoundingMode.HALF_UP);
        String sumStr = sum.toString().replaceAll(",", "\\.");
        value.postValue(sumStr);
        oldValue = sumStr;
    }

    public void calculate(String currency) {
        if (rates == null) return;
        currentCurrency = currency;
        rate = rates.getCurrencyRate(currency);
        oldValue = "1";
        value.postValue(oldValue);
        oldRub = rate.toString();
        rub.postValue(oldRub);
    }

    public MutableLiveData<String> getValue() {
        return value;
    }

    public MutableLiveData<String> getRub() {
        return rub;
    }

    public void setRates(Currencies rates) {
        this.rates = rates;
    }
}
