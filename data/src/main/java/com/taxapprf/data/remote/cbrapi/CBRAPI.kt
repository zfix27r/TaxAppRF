package com.taxapprf.data.remote.cbrapi

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface CBRAPI {
    @GET("XML_daily.asp")
    fun getCurrency(
        @Query("date_req") date: String
    ): Call<Currency>
}