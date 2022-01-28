package com.taxapprf.taxapp.ui.rates.today;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.taxapprf.taxapp.retrofit2.Controller;
import com.taxapprf.taxapp.retrofit2.Currencies;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RatesTodayViewModel extends ViewModel {
    private MutableLiveData<Currencies> currencies;

    public RatesTodayViewModel() {
        currencies = new MutableLiveData<>();
        Date currentDate = new Date();
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String dateText = dateFormat.format(currentDate);
        Controller ctrl = new Controller(dateText);
        Call<Currencies> currenciesCall = ctrl.prepareCurrenciesCall();
        currenciesCall.enqueue(new Callback<Currencies>() {
            @Override
            public void onResponse(Call<Currencies> call, Response<Currencies> response) {
                if (response.isSuccessful()) {
                    currencies.setValue(response.body());
                } else {
                    //....
                }
            }

            @Override
            public void onFailure(Call<Currencies> call, Throwable t) {

            }
        });
    }

    public MutableLiveData<Currencies> getCurrencies() {
        return currencies;
    }
}
