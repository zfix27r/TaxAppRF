package com.taxapprf.taxapp.retrofit2;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;

public class Controller {
    private String date;

    public Controller(String date) {
        this.date = date;

    }

    static final String BASE_URL = "https://www.cbr.ru/scripts/";

    public Call<Currencies> prepareCurrenciesCall() {
        Retrofit retrofit = new Retrofit.Builder().baseUrl(BASE_URL)
                .addConverterFactory(SimpleXmlConverterFactory.create()).build();

        CbrAPI cbrAPI = retrofit.create(CbrAPI.class);

        Call<Currencies> call = cbrAPI.loadCurrencies(date);
        return call;
    }
}


