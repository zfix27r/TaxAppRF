package com.taxapprf.taxapp.retrofit2;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface CbrAPI {

    @GET("XML_daily.asp")
    Call<Currencies> loadCurrencies(@Query("date_req") String date);
}




