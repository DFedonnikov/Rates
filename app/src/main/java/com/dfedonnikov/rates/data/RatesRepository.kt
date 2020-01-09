package com.dfedonnikov.rates.data

import com.dfedonnikov.rates.domain.Currency
import com.dfedonnikov.rates.domain.RatesState
import com.dfedonnikov.rates.domain.RatesData
import com.dfedonnikov.rates.network.RatesService
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.subjects.BehaviorSubject
import java.lang.Exception
import javax.inject.Inject

interface RatesRepository {

    fun getLastRates(): Observable<RatesState>
    fun updateRates(base: String): Single<RatesData>
    fun getState(): RatesState?
    fun updateState(ratesState: RatesState)
}

class RatesRepositoryImpl @Inject constructor(private val api: RatesService) : RatesRepository {

    private val ratesSubject = BehaviorSubject.create<RatesState>()
    private var ratesState: RatesState? = null

    override fun getLastRates(): Observable<RatesState> = ratesSubject

    override fun updateRates(base: String): Single<RatesData> = getRates(base).doOnError { ratesSubject.onError(it) }

    private fun getRates(base: String): Single<RatesData> {
        return api.getRates(base)
            .map { response ->
                val currencies = response.rates
                    .filterKeys {
                        val currency = try {
                            Currency.valueOf(it)
                        } catch (e: Exception) {
                            null
                        }
                        currency != null
                    }
                    .mapKeys { Currency.valueOf(it.key) }
                RatesData(base = Currency.valueOf(base), currencies = currencies)
            }
    }

    override fun getState(): RatesState? = ratesState

    override fun updateState(ratesState: RatesState) {
        this.ratesState = ratesState
        ratesSubject.onNext(ratesState)
    }

}