package com.taxapprf.data.remote.cbrapi;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface CbrAPI {

    @GET("XML_daily.asp")
    Call<Currencies> loadCurrencies(@Query("date_req") String date);
}




