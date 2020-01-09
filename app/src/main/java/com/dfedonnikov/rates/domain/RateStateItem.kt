package com.dfedonnikov.rates.domain

import java.math.BigDecimal

data class RateStateItem(val currency: Currency,
                         val amount: BigDecimal,
                         val ratioToBase: BigDecimal)