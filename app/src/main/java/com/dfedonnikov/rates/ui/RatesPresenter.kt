package com.dfedonnikov.rates.ui

import com.dfedonnikov.rates.R
import com.hannesdorfmann.mosby3.mvp.MvpBasePresenter
import java.util.*
import javax.inject.Inject

class RatesPresenter @Inject constructor() : MvpBasePresenter<RatesView>() {

    private var list = LinkedList<RateItem>()

    fun init() {
        list.addAll(
            listOf(
                RateItem(
                    title = "USD",
                    subtitle = "United States Dollar",
                    amount = "",
                    icon = R.drawable.ic_usa
                ),
                RateItem(
                    title = "GBP",
                    subtitle = "Sterling Pound",
                    amount = "",
                    icon = R.drawable.ic_uk
                ),
                RateItem(
                    title = "EUR",
                    subtitle = "EURO",
                    amount = "",
                    icon = R.drawable.ic_eu
                )
            )
        )
        ifViewAttached { it.renderRates(list.copy()) }
    }

    fun onItemClicked(item: RateItem) {
        if (list.first == item) return
        swapItemToFirstPosition(item)
        ifViewAttached { it.renderRates(list.copy()) }
    }

    private fun swapItemToFirstPosition(item: RateItem) {
        list.remove(item)
        list.addFirst(item)
    }

    fun onInputChanged(item: RateItem) {
    }

}

private fun LinkedList<RateItem>.copy(): List<RateItem> = map { it.copy() }
