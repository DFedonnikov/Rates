package com.dfedonnikov.rates.di


import com.dfedonnikov.rates.data.RatesRepository
import com.dfedonnikov.rates.data.RatesRepositoryImpl
import com.dfedonnikov.rates.domain.RatesInteractor
import com.dfedonnikov.rates.domain.RatesInteractorImpl
import dagger.Binds
import dagger.Module

@Module
interface AppModule {

    @Binds
    fun getRatesInteractor(impl: RatesInteractorImpl): RatesInteractor

    @Binds
    fun getRatesRepository(impl: RatesRepositoryImpl): RatesRepository

}