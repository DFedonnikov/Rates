package com.dfedonnikov.rates.domain

import java.math.BigDecimal

class RatesData(val base: Currency, val currencies: Map<Currency, BigDecimal>)