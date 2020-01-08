package com.dfedonnikov.rates.di

import com.dfedonnikov.rates.ui.MainActivity
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class, NetworkModule::class])
interface AppComponent {

    fun inject(activity: MainActivity)
}