package com.dfedonnikov.rates.network

import com.dfedonnikov.rates.model.RatesResponse
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Query

interface RatesService {

    @GET("/latest")
    fun getRates(@Query("base") currency: String): Observable<RatesResponse>
}