package com.dfedonnikov.rates.network

import com.dfedonnikov.rates.data.RatesResponse
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface RatesService {

    @GET("/latest")
    fun getRates(@Query("base") currency: String): Single<RatesResponse>
}