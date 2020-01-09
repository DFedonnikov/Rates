package com.dfedonnikov.rates.domain

import java.math.BigDecimal
import java.util.*

data class RatesState(val currentCurrency: Currency = Currency.EUR,
                      val currentAmount: BigDecimal = BigDecimal.ZERO,
                      private val rates: LinkedList<RateStateItem> = LinkedList()) {

    fun getRates() = LinkedList(rates)
}