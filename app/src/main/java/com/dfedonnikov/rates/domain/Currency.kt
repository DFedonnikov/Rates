package com.dfedonnikov.rates.domain

import com.dfedonnikov.rates.R

enum class Currency(val title: String, val subtitle: String, val icon: Int) {
    EUR("EUR", "Euro", R.drawable.ic_eu),
    USD("USD", "US Dollar", R.drawable.ic_usa),
    GBP("GBP", "Sterling Pound", R.drawable.ic_uk),
    RUB("RUB", "Rouble", R.drawable.ic_russia),
    AUD("AUD", "Australian Dollar", R.drawable.ic_australia),
    BGN("BGN", "Bulgarian Lev", R.drawable.ic_bulgaria),
    BRL("BRL", "Brazilian Real", R.drawable.ic_brazil),
    CAD("CAD", "Canadian Dollar", R.drawable.ic_canada),
    CHF("CHF", "Swiss Franc", R.drawable.ic_switzerland),
    CNY("CNY", "Yuan Renminbi", R.drawable.ic_china),
    CZK("CZK", "Czech Koruna", R.drawable.ic_czech),
    DKK("DKK", "Danish Krone", R.drawable.ic_denmark),
    HKD("HKD", "Hong Kong Dollar", R.drawable.ic_hong_kong),
    HRK("HRK", "Kuna", R.drawable.ic_croatia),
    HUF("HUF", "Forint", R.drawable.ic_hungary),
    IDR("IDR", "Rupiah", R.drawable.ic_indonesia),
    ILS("ILS", "New Israeli Sheqel", R.drawable.ic_israel),
    INR("INR", "Indian Rupee", R.drawable.ic_india),
    ISK("ISK", "Iceland Krona", R.drawable.ic_iceland),
    JPY("JPY", "Yen", R.drawable.ic_japan),
    KRW("KRW", "Won", R.drawable.ic_korea),
    MXN("MXN", "Mexican Peso", R.drawable.ic_mexico),
    MYR("MYR", "Malaysian Ringgit", R.drawable.ic_malaysia),
    NOK("NOK", "Norwegian Krone", R.drawable.ic_norway),
    NZD("NZD", "New Zealand Dollar", R.drawable.ic_new_zealand),
    PHP("PHP", "Philippine Peso", R.drawable.ic_philippines),
    PLN("PLN", "Zloty", R.drawable.ic_poland),
    RON("RON", "Romanian Leu", R.drawable.ic_romania),
    SEK("SEK", "Swedish Krona", R.drawable.ic_sweden),
    SGD("SGD", "Singapore Dollar", R.drawable.ic_singapore),
    THB("THB", "Baht", R.drawable.ic_thailand),
    TRY("TRY", "Turkish Lira", R.drawable.ic_turkey),
    ZAR("ZAR", "Rand", R.drawable.ic_lesotho)
}