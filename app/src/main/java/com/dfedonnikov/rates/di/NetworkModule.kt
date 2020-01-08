package com.dfedonnikov.rates.di

import com.dfedonnikov.rates.network.RatesService
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

@Module
class NetworkModule {

    @Provides
    fun getRetrofit(): Retrofit = Retrofit.Builder()
        .baseUrl("https://revolut.duckdns.org")
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    @Provides
    fun getRatesService(retrofit: Retrofit): RatesService = retrofit.create(RatesService::class.java)
}