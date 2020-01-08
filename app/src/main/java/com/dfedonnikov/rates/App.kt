package com.dfedonnikov.rates

import android.app.Application
import com.dfedonnikov.rates.di.AppComponent
import com.dfedonnikov.rates.di.DaggerAppComponent

class App : Application() {

    val component: AppComponent by lazy {
        DaggerAppComponent
            .builder()
            .build()
    }
}