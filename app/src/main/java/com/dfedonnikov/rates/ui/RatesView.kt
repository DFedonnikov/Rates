package com.dfedonnikov.rates.ui

import com.hannesdorfmann.mosby3.mvp.MvpView

interface RatesView: MvpView {

    fun renderRates(list: List<RateItem>)
}