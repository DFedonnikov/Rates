package com.dfedonnikov.rates.model

import com.google.gson.annotations.SerializedName
import java.math.BigDecimal

class RatesResponse(@SerializedName("base") val base: String,
                    @SerializedName("rates") val rates: Map<String, BigDecimal>)