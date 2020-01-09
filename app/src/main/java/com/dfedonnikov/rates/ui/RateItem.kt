package com.dfedonnikov.rates.ui

data class RateItem(val title: String,
                    val subtitle: String,
                    var amount: String,
                    val icon: Int,
                    val isBase: Boolean = false)